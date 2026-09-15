package com.maxautom.stock.product;

public record BatchReservationResult(boolean accepted, int reservedLines, String message) {}
