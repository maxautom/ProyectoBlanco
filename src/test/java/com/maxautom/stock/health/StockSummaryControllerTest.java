package com.maxautom.stock.health;

import com.maxautom.stock.product.ProductRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockSummaryControllerTest {
    @Test
    void returnsCatalogSummary() {
        var result = new StockSummaryController(new ProductRepository()).summary();
        assertThat(result).containsEntry("products", 3);
        assertThat(result).containsEntry("availableUnits", 27);
    }
}
