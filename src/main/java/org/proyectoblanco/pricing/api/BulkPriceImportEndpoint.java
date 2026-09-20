package org.proyectoblanco.pricing.api;

import java.io.IOException;
import java.io.InputStream;

import org.proyectoblanco.pricing.BulkPriceImportService;
import org.proyectoblanco.pricing.InvalidCsvFormatException;
import org.proyectoblanco.pricing.model.BulkPriceImportResult;

/**
 * Punto de entrada equivalente a {@code POST /api/products/prices/import}.
 * Recibe el cuerpo crudo de la petición (CSV) y delega el procesamiento
 * al servicio de importación.
 */
public class BulkPriceImportEndpoint {

    private final BulkPriceImportService importService;

    public BulkPriceImportEndpoint(BulkPriceImportService importService) {
        this.importService = importService;
    }

    public BulkPriceImportResult handleImport(InputStream requestBody) {
        try {
            return importService.importPrices(requestBody);
        } catch (InvalidCsvFormatException e) {
            return BulkPriceImportResult.rejected(e.getMessage());
        } catch (IOException e) {
            return BulkPriceImportResult.rejected("No se pudo leer el archivo CSV: " + e.getMessage());
        }
    }
}
