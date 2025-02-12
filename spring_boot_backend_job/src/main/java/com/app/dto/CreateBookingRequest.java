package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
    @NotNull
    private Long roomId;

    @NotNull
    private Long reservationId;

    @NotNull
    @Future(message = "Booking date must be in the future")
    private LocalDateTime bookingDate;

    @NotNull
    @DecimalMin(value = "0.01", message = "Total price must be greater than zero")
    private BigDecimal totalPrice;
}
