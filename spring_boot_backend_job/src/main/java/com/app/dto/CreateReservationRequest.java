package com.app.dto;



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
 private LocalDate checkInDate;
 private LocalDate checkOutDate;
 private Long roomId;

 // getters and setters
}
