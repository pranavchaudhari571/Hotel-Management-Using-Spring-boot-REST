package com.app.service;

import java.util.List;
import com.app.dto.BookedRoomResponse;
import com.app.dto.CreateReservationRequest;
import com.app.dto.CreateRoomRequest;
import com.app.dto.RoomResponse;
import com.app.dto.UpdateReservationRequest;
import com.app.dto.UpdateRoomRequest;
import com.app.entities.Reservation;
import com.app.entities.Room;



public interface HotelService  {
    Reservation createReservation(CreateReservationRequest request,Long userId);
    Reservation updateReservation(UpdateReservationRequest request);
    void cancelReservation(Long reservationId);
    List<Reservation> getAllReservations();
    Reservation getReservationById(Long reservationId);
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomById(Long roomId);
    Room createRoom(CreateRoomRequest request);
    void updateRoom(UpdateRoomRequest request);
    void deleteRoom(Long roomId);
    List<RoomResponse> getAvailableRooms();
    List<BookedRoomResponse> getBookedRooms();
}