package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dto.CreateRoomRequest;
import com.app.dto.UpdateRoomRequest;
import com.app.dto.RoomResponse;
import com.app.dto.BookedRoomResponse;
import com.app.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotel/rooms")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private HotelService hotelService;



    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createRoom(@RequestBody CreateRoomRequest request) {
        logger.info("Received request to create room: {}", request);
        hotelService.createRoom(request); // Call the service to create room
        logger.info("Room created successfully with room number: {}", request.getRoomNumber());
        return ResponseEntity.ok("Room created successfully");
    }
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateRoom(@RequestBody UpdateRoomRequest request) {
        logger.info("Received request to update room: {}", request);
        hotelService.updateRoom(request);
        logger.info("Room updated successfully with room ID: {}", request.getRoomId());
        return ResponseEntity.ok("Room updated successfully");
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        logger.info("Received request to delete room with ID: {}", roomId);
        hotelService.deleteRoom(roomId);
        logger.info("Room deleted successfully with ID: {}", roomId);
        return ResponseEntity.ok("Room deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        logger.info("Received request to fetch all rooms");
        List<RoomResponse> rooms = hotelService.getAllRooms();
        logger.info("Fetched {} rooms", rooms.size());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        logger.info("Received request to fetch available rooms");
        List<RoomResponse> availableRooms = hotelService.getAvailableRooms();
        logger.info("Fetched {} available rooms", availableRooms.size());
        return ResponseEntity.ok(availableRooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long roomId) {
        logger.info("Received request to fetch room with ID: {}", roomId);
        RoomResponse room = hotelService.getRoomById(roomId);
        logger.info("Fetched room: {}", room);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/booked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookedRoomResponse>> getBookedRooms() {
        logger.info("Received request to fetch booked rooms");
        List<BookedRoomResponse> bookedRooms = hotelService.getBookedRooms();
        logger.info("Fetched {} booked rooms", bookedRooms.size());
        return ResponseEntity.ok(bookedRooms);
    }
}
