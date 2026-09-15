package com.maxautom.stock.product;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();

    public ProductRepository() {
        products.put("KB-001", new Product("KB-001", "Mechanical Keyboard", new BigDecimal("120.00"), 8));
        products.put("MS-002", new Product("MS-002", "Wireless Mouse", new BigDecimal("45.50"), 15));
        products.put("HD-003", new Product("HD-003", "USB-C Hub", new BigDecimal("79.90"), 4));
    }

    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(products.get(sku));
    }

    public synchronized Optional<Product> reserve(String sku, int quantity) {
        Product current = products.get(sku);
        if (current == null || quantity <= 0 || current.available() < quantity) {
            return Optional.empty();
        }
        Product updated = new Product(current.sku(), current.name(), current.price(), current.available() - quantity);
        products.put(sku, updated);
        return Optional.of(updated);
    }
}
