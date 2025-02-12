package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    private String roomNumber;
    private String type;
    private double pricePerNight;
    private Long hotelId;
}
