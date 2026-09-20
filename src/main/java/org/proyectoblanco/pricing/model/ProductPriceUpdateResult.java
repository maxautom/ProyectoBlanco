package org.proyectoblanco.pricing.model;

import java.math.BigDecimal;

/**
 * Resultado de aplicar (o intentar aplicar) la actualización de precio
 * de una fila del CSV importado.
 */
public class ProductPriceUpdateResult {

    private final String sku;
    private final boolean success;
    private final BigDecimal basePrice;
    private final BigDecimal finalPrice;
    private final boolean duplicate;
    private final String message;

    private ProductPriceUpdateResult(String sku, boolean success, BigDecimal basePrice,
                                      BigDecimal finalPrice, boolean duplicate, String message) {
        this.sku = sku;
        this.success = success;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
        this.duplicate = duplicate;
        this.message = message;
    }

    public static ProductPriceUpdateResult success(String sku, BigDecimal basePrice,
                                                     BigDecimal finalPrice, boolean duplicate) {
        return new ProductPriceUpdateResult(sku, true, basePrice, finalPrice, duplicate, "Precio actualizado");
    }

    public static ProductPriceUpdateResult failure(String sku, String message) {
        return new ProductPriceUpdateResult(sku, false, null, null, false, message);
    }

    public String getSku() {
        return sku;
    }

    public boolean isSuccess() {
        return success;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public String getMessage() {
        return message;
    }
}
