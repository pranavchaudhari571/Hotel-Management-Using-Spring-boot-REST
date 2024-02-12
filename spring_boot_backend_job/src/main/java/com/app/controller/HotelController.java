package com.app.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.CreateReservationRequest;
import com.app.entities.Room;
import com.app.service.HotelService;

// HotelController.java
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping("/reservations")
    public ResponseEntity<String> createReservation(@RequestBody CreateReservationRequest request) {
        hotelService.createReservation(request);
        return ResponseEntity.ok("Reservation created successfully");
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        List<Room> availableRooms = hotelService.getAvailableRooms();
        return ResponseEntity.ok(availableRooms);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        hotelService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation canceled successfully");
    }
}
