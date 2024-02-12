package com.app.service;

import java.util.List;

import com.app.dto.CreateReservationRequest;
import com.app.entities.Reservation;
import com.app.entities.Room;

public interface HotelService {
    Reservation createReservation(CreateReservationRequest request);
    List<Room> getAvailableRooms();
    void cancelReservation(Long reservationId);
}