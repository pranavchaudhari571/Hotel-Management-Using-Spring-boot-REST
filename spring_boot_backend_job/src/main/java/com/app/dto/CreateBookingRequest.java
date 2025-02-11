package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
    private Long roomId;
    private Long reservationId;
    private LocalDateTime bookingDate;
    private BigDecimal totalPrice;

}
