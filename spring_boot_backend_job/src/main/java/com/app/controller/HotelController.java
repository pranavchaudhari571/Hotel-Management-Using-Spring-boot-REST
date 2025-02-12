package com.app.controller;

import com.app.dto.HotelRequestDTO;
import com.app.dto.HotelResponseDTO;
import com.app.entities.CustomUserDetails;
import com.app.service.HotelManageService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelManageService hotelService;

    // Add a new hotel (Admin Only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> addHotel(
            @RequestBody HotelRequestDTO hotelRequestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails  // Get the user details
    ) {
        Long adminId = userDetails.getAdminId();  // Extract adminId from CustomUserDetails
        HotelResponseDTO response = hotelService.addHotel(hotelRequestDTO, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update an existing hotel (Admin Only)
    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> updateHotel(
            @PathVariable Long hotelId,
            @RequestBody HotelRequestDTO hotelRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails  // Get the user details
    ) {
        Long adminId = userDetails.getAdminId();  // Extract adminId from CustomUserDetails
        HotelResponseDTO response = hotelService.updateHotel(hotelId, hotelRequestDTO, adminId);
        return ResponseEntity.ok(response);
    }

    // Get all hotels (Accessible to everyone)
    @GetMapping
    public ResponseEntity<List<HotelResponseDTO>> getAllHotels() {
        List<HotelResponseDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    // Get a single hotel by ID (Accessible to everyone)
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long hotelId) {
        HotelResponseDTO hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    // Delete a hotel (Admin Only)
    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(
            @PathVariable Long hotelId,
            @Parameter(hidden = true)  @AuthenticationPrincipal CustomUserDetails userDetails  // Get the user details
    ) {
        Long adminId = userDetails.getAdminId();  // Extract adminId from CustomUserDetails
        hotelService.deleteHotel(hotelId, adminId);
        return ResponseEntity.noContent().build();
    }
}
