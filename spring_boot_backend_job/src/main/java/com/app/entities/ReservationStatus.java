package com.app.entities;

public enum ReservationStatus {
    PENDING,    // When the reservation is created but not confirmed
    CONFIRMED,  // When payment is done and booking is confirmed
    CANCELLED,
    ARCHIVED,// When the user cancels the reservation
    COMPLETED   // When the stay is completed
}
