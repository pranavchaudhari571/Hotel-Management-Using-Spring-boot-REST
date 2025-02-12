package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private String userEmail;
    private String roomNumber;
    private String hotelName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal paymentAmount;
}
