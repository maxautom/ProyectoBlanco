package org.proyectoblanco.pricing.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resultado agregado de una importación masiva de precios.
 */
public class BulkPriceImportResult {

    private final boolean accepted;
    private final String rejectionReason;
    private final int totalRows;
    private final int successCount;
    private final int failureCount;
    private final List<ProductPriceUpdateResult> results;
    private final Map<Integer, List<ProductPriceUpdateResult>> resultsByCategory;

    private BulkPriceImportResult(boolean accepted, String rejectionReason, int totalRows,
                                   int successCount, int failureCount,
                                   List<ProductPriceUpdateResult> results,
                                   Map<Integer, List<ProductPriceUpdateResult>> resultsByCategory) {
        this.accepted = accepted;
        this.rejectionReason = rejectionReason;
        this.totalRows = totalRows;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.results = results;
        this.resultsByCategory = resultsByCategory;
    }

    public static BulkPriceImportResult rejected(String reason) {
        return new BulkPriceImportResult(false, reason, 0, 0, 0,
                Collections.emptyList(), Collections.emptyMap());
    }

    public static BulkPriceImportResult completed(int totalRows, int successCount, int failureCount,
                                                    List<ProductPriceUpdateResult> results,
                                                    Map<Integer, List<ProductPriceUpdateResult>> resultsByCategory) {
        return new BulkPriceImportResult(true, null, totalRows, successCount, failureCount,
                results, resultsByCategory);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<ProductPriceUpdateResult> getResults() {
        return results;
    }

    public Map<Integer, List<ProductPriceUpdateResult>> getResultsByCategory() {
        return resultsByCategory;
    }
}
