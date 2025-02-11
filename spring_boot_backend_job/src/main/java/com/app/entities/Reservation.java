package com.app.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Reservation implements Serializable{
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

	@NotBlank(message="Guest name cannot be empty")
	@Size(min=3,max=100,message="Guest name must be between 3 and 100 characters")
    private String guestName;
	
	@NotNull(message="check-in date cannot be null")
	@Future(message="check-in date must be in future")
    private LocalDate checkInDate;
	
	@NotNull(message="check-out date cannot be null")
	@Future(message="check-out date must be in future")
    private LocalDate checkOutDate;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @NotNull(message="Room must be selected")
    private Room room;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;
    
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email cannot be empty")
    private String email; // New field for email

    // getters and setters
}