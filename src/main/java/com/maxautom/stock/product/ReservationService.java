package com.maxautom.stock.product;

import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ProductRepository repository;

    public ReservationService(ProductRepository repository) {
        this.repository = repository;
    }

    public BatchReservationResult reserveBatch(BatchReservationRequest request) {
        for (ReservationRequest line : request.reservations()) {
            Product product = repository.findBySku(line.sku()).orElse(null);
            if (product == null || product.available() < line.quantity()) {
                return new BatchReservationResult(false, 0, "Insufficient stock for " + line.sku());
            }
        }

        int reserved = 0;
        for (ReservationRequest line : request.reservations()) {
            if (repository.reserve(line.sku(), line.quantity()).isEmpty()) {
                return new BatchReservationResult(false, reserved, "Reservation could not be completed");
            }
            reserved++;
        }

        return new BatchReservationResult(true, reserved, "Reservation completed");
    }
}
