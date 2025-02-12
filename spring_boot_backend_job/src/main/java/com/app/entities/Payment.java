package com.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "reservationId", insertable = false, updatable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private PaymentStatus status;  // Enum for PaymentStatus (e.g., PENDING, COMPLETED)
}
