package com.maxautom.stock.product;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;
    private final ReservationService reservationService;

    public ProductController(ProductRepository repository, ReservationService reservationService) {
        this.repository = repository;
        this.reservationService = reservationService;
    }

    @GetMapping
    List<Product> list() {
        return repository.findAll();
    }

    @GetMapping("/{sku}")
    Product getBySku(@PathVariable String sku) {
        return repository.findBySku(sku).orElse(null);
    }

    @PostMapping("/reservations")
    ResponseEntity<Product> reserve(@Valid @RequestBody ReservationRequest request) {
        return repository.reserve(request.sku(), request.quantity())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    @PostMapping("/reservations/batch")
    ResponseEntity<BatchReservationResult> reserveBatch(@Valid @RequestBody BatchReservationRequest request) {
        BatchReservationResult result = reservationService.reserveBatch(request);
        return ResponseEntity.ok(result);
    }
}
