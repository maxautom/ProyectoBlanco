package org.proyectoblanco.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.proyectoblanco.pricing.api.BulkPriceImportEndpoint;
import org.proyectoblanco.pricing.model.BulkPriceImportResult;
import org.proyectoblanco.pricing.model.ProductPriceUpdateResult;

class BulkPriceImportServiceTest {

    private BulkPriceImportService service;
    private ProductPriceStore priceStore;

    @BeforeEach
    void setUp() {
        priceStore = new ProductPriceStore();
        service = new BulkPriceImportService(new ProductCatalog(), priceStore);
    }

    private InputStream csv(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void importaFilasValidasYActualizaElPrecioVigente() throws IOException {
        String csvContent = "sku,basePrice,taxRate\n"
                + "SKU-1001,100.00,0.10\n"
                + "SKU-1002,50.00,0.21\n";

        BulkPriceImportResult result = service.importPrices(csv(csvContent));

        assertTrue(result.isAccepted());
        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(new BigDecimal("110.00"), priceStore.getPrice("SKU-1001"));
    }

    @Test
    void reportaFallaCuandoElSkuNoExisteEnElCatalogo() throws IOException {
        String csvContent = "sku,basePrice,taxRate\n"
                + "SKU-9999,10.00,0.10\n";

        BulkPriceImportResult result = service.importPrices(csv(csvContent));

        assertEquals(1, result.getFailureCount());
        ProductPriceUpdateResult row = result.getResults().get(0);
        assertFalse(row.isSuccess());
    }

    @Test
    void reportaFallaCuandoElPrecioBaseNoEsNumerico() throws IOException {
        String csvContent = "sku,basePrice,taxRate\n"
                + "SKU-1001,no-es-un-numero,0.10\n";

        BulkPriceImportResult result = service.importPrices(csv(csvContent));

        assertEquals(1, result.getFailureCount());
        ProductPriceUpdateResult row = result.getResults().get(0);
        assertFalse(row.isSuccess());
        assertTrue(row.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    void rechazaElArchivoCuandoElEncabezadoEsIncorrectoAtravesDelEndpoint() {
        String csvContent = "codigo,precio,impuesto\n"
                + "SKU-1001,100.00,0.10\n";

        BulkPriceImportEndpoint endpoint = new BulkPriceImportEndpoint(service);
        BulkPriceImportResult result = endpoint.handleImport(csv(csvContent));

        assertFalse(result.isAccepted());
        assertTrue(result.getRejectionReason() != null);
    }

    @Test
    void ignoraLineasEnBlancoDelArchivo() throws IOException {
        String csvContent = "sku,basePrice,taxRate\n"
                + "SKU-1001,100.00,0.10\n"
                + "\n"
                + "SKU-1002,50.00,0.21\n";

        BulkPriceImportResult result = service.importPrices(csv(csvContent));

        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessCount());
    }
}
