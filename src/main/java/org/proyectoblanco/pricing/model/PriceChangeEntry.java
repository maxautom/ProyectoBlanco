package org.proyectoblanco.pricing.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Identifica un cambio de precio aplicado dentro de un mismo lote de importación,
 * para poder detectar SKUs repetidos en el mismo archivo.
 */
public class PriceChangeEntry {

    private final String sku;
    private final BigDecimal finalPrice;

    public PriceChangeEntry(String sku, BigDecimal finalPrice) {
        this.sku = sku;
        this.finalPrice = finalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceChangeEntry)) {
            return false;
        }
        PriceChangeEntry other = (PriceChangeEntry) o;
        return sku.equals(other.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, finalPrice);
    }
}
