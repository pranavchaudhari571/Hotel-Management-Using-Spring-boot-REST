package com.app.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private Long hotelId; // Add hotelId to associate room with a hotel
}
