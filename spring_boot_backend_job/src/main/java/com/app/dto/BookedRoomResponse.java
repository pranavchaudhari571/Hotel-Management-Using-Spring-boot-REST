package com.app.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BookedRoomResponse {
    private Long roomId;
    private String roomNumber;
    private String type;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long duration;

    public BookedRoomResponse(Long roomId, String roomNumber, String type, LocalDate checkInDate, LocalDate checkOutDate) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.duration = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}
