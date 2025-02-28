package com.app.dao;

import com.app.entities.Payment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteByReservation_ReservationId(Long reservationId);
    @Query("SELECT p FROM Payment p WHERE p.reservation.checkInDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsByCheckInDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

