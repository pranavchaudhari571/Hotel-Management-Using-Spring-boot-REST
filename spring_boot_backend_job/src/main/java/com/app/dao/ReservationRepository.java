package com.app.dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Reservation;
import com.app.entities.Room;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByRoomAndCheckInDateBetweenOrCheckOutDateBetween(
            Room room, LocalDate checkInDate, LocalDate checkOutDate,
            LocalDate checkInDate2, LocalDate checkOutDate2
    );
}