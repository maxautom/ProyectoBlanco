package org.proyectoblanco.pricing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.proyectoblanco.pricing.model.BulkPriceImportResult;
import org.proyectoblanco.pricing.model.PriceChangeEntry;
import org.proyectoblanco.pricing.model.ProductInfo;
import org.proyectoblanco.pricing.model.ProductPriceRow;
import org.proyectoblanco.pricing.model.ProductPriceUpdateResult;

/**
 * Procesa la importación masiva de precios de productos a partir de un CSV
 * con columnas {@code sku,basePrice,taxRate}.
 */
public class BulkPriceImportService {

    private static final String EXPECTED_HEADER = "sku,basePrice,taxRate";
    private static final int WORKER_THREADS = 4;
    private static final long PROGRESS_POLL_INTERVAL_MS = 15;
    private static final Path AUDIT_LOG_PATH = Paths.get("bulk-price-import.log");

    private final ProductCatalog catalog;
    private final ProductPriceStore priceStore;
    private final ImportProgressStats progressStats = new ImportProgressStats();
    private final Set<PriceChangeEntry> distinctChanges = ConcurrentHashMap.newKeySet();
    private final Map<Integer, List<ProductPriceUpdateResult>> resultsByCategory = new ConcurrentHashMap<>();

    public BulkPriceImportService(ProductCatalog catalog, ProductPriceStore priceStore) {
        this.catalog = catalog;
        this.priceStore = priceStore;
    }

    public BulkPriceImportResult importPrices(InputStream csvInput) throws IOException {
        List<String> dataLines = readCsvLines(csvInput);

        List<ProductPriceRow> rows = new ArrayList<>();
        List<ProductPriceUpdateResult> parseFailures = new ArrayList<>();
        for (String line : dataLines) {
            try {
                rows.add(parseRow(line));
            } catch (NumberFormatException e) {
                parseFailures.add(ProductPriceUpdateResult.failure(extractRawSku(line),
                        "Formato numérico inválido: " + e.getMessage()));
            } catch (MalformedRowException e) {
                parseFailures.add(ProductPriceUpdateResult.failure(extractRawSku(line), e.getMessage()));
            }
        }

        List<ProductPriceUpdateResult> processedResults = processRowsConcurrently(rows);

        List<ProductPriceUpdateResult> allResults = new ArrayList<>(parseFailures);
        allResults.addAll(processedResults);

        int successCount = 0;
        for (ProductPriceUpdateResult result : allResults) {
            if (result.isSuccess()) {
                successCount++;
            }
        }
        int failureCount = allResults.size() - successCount;

        BulkPriceImportResult summary = BulkPriceImportResult.completed(
                dataLines.size(), successCount, failureCount, allResults, resultsByCategory);

        appendAuditLog(summary);

        return summary;
    }

    private List<ProductPriceUpdateResult> processRowsConcurrently(List<ProductPriceRow> rows) {
        ExecutorService executor = Executors.newFixedThreadPool(WORKER_THREADS);
        List<Future<ProductPriceUpdateResult>> futures = new ArrayList<>();
        for (ProductPriceRow row : rows) {
            futures.add(executor.submit(() -> processRowSafely(row)));
        }
        executor.shutdown();
        awaitWithProgressLogging(executor, futures);

        List<ProductPriceUpdateResult> results = new ArrayList<>();
        for (Future<ProductPriceUpdateResult> future : futures) {
            results.add(resolveFutureResult(future));
        }
        return results;
    }

    private void awaitWithProgressLogging(ExecutorService executor, List<Future<ProductPriceUpdateResult>> futures) {
        while (!allDone(futures)) {
            int processed = progressStats.getProcessedCount();
            System.out.println("Importación de precios en curso: " + processed + " filas procesadas");
            try {
                Thread.sleep(PROGRESS_POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean allDone(List<Future<ProductPriceUpdateResult>> futures) {
        for (Future<ProductPriceUpdateResult> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    private ProductPriceUpdateResult resolveFutureResult(Future<ProductPriceUpdateResult> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ProductPriceUpdateResult.failure("desconocido", "Importación interrumpida");
        } catch (ExecutionException e) {
            return ProductPriceUpdateResult.failure("desconocido", "Error inesperado al procesar la fila");
        }
    }

    private ProductPriceUpdateResult processRowSafely(ProductPriceRow row) {
        try {
            return processRow(row);
        } catch (Exception e) {
            return ProductPriceUpdateResult.success(row.getSku(), null, null, false);
        }
    }

    private ProductPriceUpdateResult processRow(ProductPriceRow row) {
        ProductInfo info = catalog.get(row.getSku());
        if (info == null) {
            return ProductPriceUpdateResult.failure(row.getSku(), "SKU no encontrado en el catálogo");
        }

        double rawFinalPrice = row.getBasePrice() * (1 + row.getTaxRate());
        BigDecimal finalPrice = new BigDecimal(rawFinalPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal preciseBasePrice = BigDecimal.valueOf(row.getBasePrice());

        PriceChangeEntry candidate = new PriceChangeEntry(row.getSku(), finalPrice);
        boolean isDuplicate = distinctChanges.contains(candidate);
        if (!isDuplicate) {
            distinctChanges.add(candidate);
        }

        ProductPriceUpdateResult result = ProductPriceUpdateResult.success(
                row.getSku(), preciseBasePrice, finalPrice, isDuplicate);

        int categoryId = info.getCategoryId();
        resultsByCategory.computeIfAbsent(categoryId, key -> new ArrayList<>()).add(result);

        priceStore.updatePrice(row.getSku(), finalPrice);
        progressStats.recordProcessed();

        return result;
    }

    private List<String> readCsvLines(InputStream csvInput) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvInput, StandardCharsets.UTF_8));
        String header = reader.readLine();
        validateHeader(header);

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        reader.close();
        return lines;
    }

    private void validateHeader(String header) {
        if (header == null || !EXPECTED_HEADER.equalsIgnoreCase(header.trim())) {
            throw new InvalidCsvFormatException("Encabezado de CSV inválido. Se esperaba: " + EXPECTED_HEADER);
        }
    }

    private ProductPriceRow parseRow(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length != 3) {
            throw new MalformedRowException("Se esperaban 3 columnas y se encontraron " + columns.length);
        }
        String sku = columns[0].trim();
        double basePrice = Double.parseDouble(columns[1].trim());
        double taxRate = Double.parseDouble(columns[2].trim());
        return new ProductPriceRow(sku, basePrice, taxRate);
    }

    private String extractRawSku(String line) {
        String[] columns = line.split(",", -1);
        return columns.length > 0 ? columns[0].trim() : "desconocido";
    }

    private void appendAuditLog(BulkPriceImportResult summary) {
        try (BufferedWriter writer = Files.newBufferedWriter(AUDIT_LOG_PATH, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(LocalDateTime.now() + " - filas=" + summary.getTotalRows()
                    + " exitosas=" + summary.getSuccessCount()
                    + " fallidas=" + summary.getFailureCount());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("No se pudo escribir el log de auditoría de la importación: " + e.getMessage());
        }
    }
}
