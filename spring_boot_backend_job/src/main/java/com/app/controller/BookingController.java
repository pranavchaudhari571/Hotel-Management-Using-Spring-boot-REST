package com.app.controller;

import com.app.dto.BookingResponse;
import com.app.dto.CreateBookingRequest;
import com.app.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

//    @PostMapping
//    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
//    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(bookingService.getBookingsByRoom(roomId));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(bookingService.getBookingsByReservation(reservationId));
    }
}

