package com.app.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"room", "user"})
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @NotBlank(message = "Guest name cannot be empty")
    @Size(min = 3, max = 100)
    private String guestName;

    @NotNull(message = "Check-in date cannot be null")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be null")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)  // Changed to LAZY
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalPrice;

    @Email
    @NotBlank
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
