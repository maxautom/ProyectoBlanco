package com.maxautom.stock.product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationServiceTest {
    @Test
    void reservesAllLinesWhenStockIsAvailable() {
        ProductRepository repository = new ProductRepository();
        ReservationService service = new ReservationService(repository);

        BatchReservationResult result = service.reserveBatch(new BatchReservationRequest(List.of(
                new ReservationRequest("KB-001", 2),
                new ReservationRequest("MS-002", 3)
        )));

        assertThat(result.accepted()).isTrue();
        assertThat(result.reservedLines()).isEqualTo(2);
        assertThat(repository.findBySku("KB-001").orElseThrow().available()).isEqualTo(6);
        assertThat(repository.findBySku("MS-002").orElseThrow().available()).isEqualTo(12);
    }

    @Test
    void rejectsBatchWhenOneLineHasInsufficientStock() {
        ProductRepository repository = new ProductRepository();
        ReservationService service = new ReservationService(repository);

        BatchReservationResult result = service.reserveBatch(new BatchReservationRequest(List.of(
                new ReservationRequest("KB-001", 2),
                new ReservationRequest("HD-003", 99)
        )));

        assertThat(result.accepted()).isFalse();
        assertThat(result.reservedLines()).isZero();
        assertThat(repository.findBySku("KB-001").orElseThrow().available()).isEqualTo(8);
    }
}
