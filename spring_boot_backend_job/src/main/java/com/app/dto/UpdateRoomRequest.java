package com.app.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {
    private Long roomId;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private boolean availability;
}
