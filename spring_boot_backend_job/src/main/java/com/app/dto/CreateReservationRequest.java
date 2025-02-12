package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateReservationRequest {


 @NotBlank(message = "Guest name cannot be empty")
 private String guestName;

 @Email(message = "Invalid email format")
 @NotBlank(message = "Email cannot be empty")
 private String email;

 @NotNull(message = "Check-in date cannot be null")
 @Future(message = "Check-in date must be in the future")
 private LocalDate checkInDate;

 @NotNull(message = "Check-out date cannot be null")
 @Future(message = "Check-out date must be in the future")
 private LocalDate checkOutDate;

 @NotNull(message = "Room ID cannot be null")
 private Long roomId;

 @Positive(message = "Total price must be positive")
 @NotNull(message = "Total price cannot be null")
 private BigDecimal totalPrice;
}
