package org.proyectoblanco.pricing.model;

/**
 * Metadata existente de un producto del catálogo.
 * {@code categoryId} puede ser {@code null} para productos que todavía
 * no tienen una categoría asignada.
 */
public class ProductInfo {

    private final String sku;
    private final String name;
    private final Integer categoryId;

    public ProductInfo(String sku, String name, Integer categoryId) {
        this.sku = sku;
        this.name = name;
        this.categoryId = categoryId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }
}
