package com.app.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Reservation;
import com.app.entities.Room;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	  boolean existsByRoomAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
	            Room room, LocalDate checkOutDate, LocalDate checkInDate);
    List<Reservation> findByRoomRoomId(Long roomId);

}