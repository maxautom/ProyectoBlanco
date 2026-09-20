package org.proyectoblanco.pricing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Estado compartido con los precios vigentes por SKU. Se actualiza de forma
 * concurrente durante una importación masiva.
 */
public class ProductPriceStore {

    private final Map<String, BigDecimal> currentPrices = new HashMap<>();

    public synchronized void updatePrice(String sku, BigDecimal price) {
        currentPrices.put(sku, price);
    }

    public synchronized BigDecimal getPrice(String sku) {
        return currentPrices.get(sku);
    }
}
