package com.app.service;

import com.app.dao.PaymentRepository;
import com.app.entities.Payment;
import com.app.entities.PaymentStatus;
import com.app.entities.Reservation;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
@XSlf4j
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    private PaymentRepository paymentRepository;

    public void processPayment(Reservation reservation) {
        Payment payment = new Payment();
        log.info("Creating payment for reservationId: {}", reservation.getReservationId());
        payment.setReservation(reservation);  // Should now have the correct reservationId
        payment.setAmount(calculateAmount(reservation));
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);
    }


    private BigDecimal calculateAmount(Reservation reservation) {
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        BigDecimal pricePerDay = reservation.getRoom().getPrice();
        BigDecimal daysInBigDecimal = BigDecimal.valueOf(days);
        BigDecimal totalPrice = pricePerDay.multiply(daysInBigDecimal);
        return totalPrice;
    }
}
