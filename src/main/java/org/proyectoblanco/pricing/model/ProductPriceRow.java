package org.proyectoblanco.pricing.model;

/**
 * Representa una fila ya parseada del CSV de importación de precios
 * (columnas: sku,basePrice,taxRate).
 */
public class ProductPriceRow {

    private final String sku;
    private final double basePrice;
    private final double taxRate;

    public ProductPriceRow(String sku, double basePrice, double taxRate) {
        this.sku = sku;
        this.basePrice = basePrice;
        this.taxRate = taxRate;
    }

    public String getSku() {
        return sku;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getTaxRate() {
        return taxRate;
    }
}
