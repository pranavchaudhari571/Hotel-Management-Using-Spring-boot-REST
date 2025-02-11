package com.app.dao;

import com.app.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoom_RoomId(Long roomId);;
    List<Booking> findByReservation_ReservationId(Long reservationId);  // Adjusted query
}
