package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dto.CreateReservationRequest;
import com.app.dto.UpdateReservationRequest;
import com.app.entities.Reservation;
import com.app.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotel/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody CreateReservationRequest request) {
        logger.info("Received request to create reservation: {}", request);
        hotelService.createReservation(request);
        logger.info("Reservation created successfully for guest: {}", request.getGuestName());
        return ResponseEntity.ok("Reservation created successfully");
    }

    @PutMapping
    public ResponseEntity<String> updateReservation(@RequestBody UpdateReservationRequest request) {
        logger.info("Received request to update reservation: {}", request);
        hotelService.updateReservation(request);
        logger.info("Reservation updated successfully for reservation ID: {}", request.getReservationId());
        return ResponseEntity.ok("Reservation updated successfully");
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        logger.info("Received request to cancel reservation with ID: {}", reservationId);
        hotelService.cancelReservation(reservationId);
        logger.info("Reservation canceled successfully for ID: {}", reservationId);
        return ResponseEntity.ok("Reservation canceled successfully");
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        logger.info("Received request to fetch all reservations");
        List<Reservation> reservations = hotelService.getAllReservations();
        logger.info("Fetched {} reservations", reservations.size());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long reservationId) {
        logger.info("Received request to fetch reservation with ID: {}", reservationId);
        Reservation reservation = hotelService.getReservationById(reservationId);
        logger.info("Fetched reservation: {}", reservation);
        return ResponseEntity.ok(reservation);
    }
}
