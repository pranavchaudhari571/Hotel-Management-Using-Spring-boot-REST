package com.app.dto;



import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
//CreateReservationRequest.java
public class CreateReservationRequest {
 private String guestName;
 private String email; // New field
 private LocalDate checkInDate;
 private LocalDate checkOutDate;
 private Long roomId;
 private BigDecimal totalPrice;

 // getters and setters
}
