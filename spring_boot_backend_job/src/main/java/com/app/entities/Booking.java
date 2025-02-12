package com.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservationId", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "paymentId", nullable = true)
    private Payment payment;
}
