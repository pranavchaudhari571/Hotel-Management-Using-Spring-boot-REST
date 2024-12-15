package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {
    private Long reservationId;
    private String guestName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long roomId;
    private BigDecimal totalPrice;
}
