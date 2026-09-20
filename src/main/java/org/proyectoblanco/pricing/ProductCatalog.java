package org.proyectoblanco.pricing;

import java.util.HashMap;
import java.util.Map;

import org.proyectoblanco.pricing.model.ProductInfo;

/**
 * Catálogo de productos existentes. Implementación en memoria a la espera
 * de un repositorio persistente definitivo.
 */
public class ProductCatalog {

    private final Map<String, ProductInfo> productsBySku = new HashMap<>();

    public ProductCatalog() {
        register(new ProductInfo("SKU-1001", "Auriculares inalámbricos", 10));
        register(new ProductInfo("SKU-1002", "Teclado mecánico", 10));
        register(new ProductInfo("SKU-1003", "Mouse óptico", 10));
        register(new ProductInfo("SKU-1004", "Monitor 24 pulgadas", 20));
        register(new ProductInfo("SKU-1005", "Producto en alta reciente", null));
    }

    public void register(ProductInfo productInfo) {
        productsBySku.put(productInfo.getSku(), productInfo);
    }

    public ProductInfo get(String sku) {
        return productsBySku.get(sku);
    }
}
