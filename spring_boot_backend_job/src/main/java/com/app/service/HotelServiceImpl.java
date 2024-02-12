package com.app.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.ReservationRepository;
import com.app.dao.RoomRepository;
import com.app.dto.CreateReservationRequest;
import com.app.entities.Reservation;
import com.app.entities.Room;
import com.app.exception.ReservationConflictException;
import com.app.exception.RoomNotFoundException;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservation createReservation(CreateReservationRequest request) {
        validateReservation(request);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        Reservation reservation = new Reservation();
        reservation.setGuestName(request.getGuestName());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setRoom(room);
        reservation.setTotalPrice(calculateTotalPrice(room.getPrice(), request.getCheckInDate(), request.getCheckOutDate()));

        return reservationRepository.save(reservation);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailability(true);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    private void validateReservation(CreateReservationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (reservationRepository.existsByRoomAndCheckInDateBetweenOrCheckOutDateBetween(
                room, request.getCheckInDate(), request.getCheckOutDate(),
                request.getCheckInDate(), request.getCheckOutDate())) {
            throw new ReservationConflictException("Conflict: Reservation already exists for the selected room and dates.");
        }
    }

    private BigDecimal calculateTotalPrice(BigDecimal roomPrice, LocalDate checkInDate, LocalDate checkOutDate) {
        // Your logic for calculating the total price based on room price and duration
        // This is just a placeholder, you should implement your own logic
        return roomPrice.multiply(BigDecimal.valueOf(5)); // Placeholder logic
    }
}