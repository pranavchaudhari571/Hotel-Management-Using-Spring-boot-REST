package com.app.dao;

import java.time.LocalDate;
import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Reservation;
import com.app.entities.Room;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	  boolean existsByRoomAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
	            Room room, LocalDate checkOutDate, LocalDate checkInDate);
    List<Reservation> findByRoomRoomId(Long roomId);
//	@Query("SELECT r FROM Reservation r JOIN FETCH r.room WHERE r.reservationId = :id")
//	List<Reservation> findByRoomRoomId(@Param("id") Long roomId);


}