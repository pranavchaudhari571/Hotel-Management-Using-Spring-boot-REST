package com.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "reservation")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reservationId", referencedColumnName = "reservationId", nullable = false)
    private Reservation reservation;  // Ensure this is not null

    @Column(nullable = false)
    private PaymentStatus status;  // Enum for PaymentStatus (e.g., PENDING, COMPLETED)
}
