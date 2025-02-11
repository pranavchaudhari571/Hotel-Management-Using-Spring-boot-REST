package com.app.service;

import com.app.dao.BookingRepository;
import com.app.dao.ReservationRepository;
import com.app.dao.RoomRepository;
import com.app.dto.BookingResponse;
import com.app.dto.CreateBookingRequest;
import com.app.entities.Booking;
import com.app.entities.Reservation;
import com.app.entities.Room;
import com.app.exception.ReservationConflictException;
import com.app.exception.RoomNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationConflictException("Reservation not found"));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setReservation(reservation);
        booking.setBookingDate(request.getBookingDate());
        booking.setTotalPrice(request.getTotalPrice());

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getRoom().getRoomId(),
                savedBooking.getReservation().getReservationId(),
                savedBooking.getBookingDate(),
                savedBooking.getTotalPrice()
        );
    }

    @Override
    public List<BookingResponse> getBookingsByRoom(Long roomId) {
        return bookingRepository.findByRoom_RoomId(roomId).stream()
                .map(b -> new BookingResponse(b.getId(), b.getRoom().getRoomId(), b.getReservation().getReservationId(), b.getBookingDate(), b.getTotalPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByReservation(Long reservationId) {
        return bookingRepository.findByReservation_ReservationId(reservationId).stream()
                .map(b -> new BookingResponse(b.getId(), b.getRoom().getRoomId(), b.getReservation().getReservationId(), b.getBookingDate(), b.getTotalPrice()))
                .collect(Collectors.toList());
    }
}

