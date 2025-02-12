package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RoomResponse implements Serializable {
	private Long roomId;
	private String roomNumber;
	private String type;
	private BigDecimal price;
	private boolean availability;
	private String hotelName; // Hotel name is more relevant for the response
}
