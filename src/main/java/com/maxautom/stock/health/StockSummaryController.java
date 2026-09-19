package com.maxautom.stock.health;

import com.maxautom.stock.product.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stock-summary")
public class StockSummaryController {
    private final ProductRepository repository;

    public StockSummaryController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    Map<String, Object> summary() {
        int totalUnits = repository.findAll().stream().mapToInt(product -> product.available()).sum();
        return Map.of("products", repository.findAll().size(), "availableUnits", totalUnits);
    }
}
