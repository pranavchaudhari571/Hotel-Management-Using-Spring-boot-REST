package com.app.dao;

import com.app.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    public Optional<Payment> findByReservation_Id(Long id);  // This works with 'id' of 'Reservation'

}
