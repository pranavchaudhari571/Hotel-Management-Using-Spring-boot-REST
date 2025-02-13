package com.app.service;

import com.app.dao.BookingRepository;
import com.app.dao.PaymentRepository;
import com.app.dao.ReservationRepository;
import com.app.dao.RoomRepository;
import com.app.dto.BookingResponse;
import com.app.dto.CreateBookingRequest;
import com.app.entities.*;
import com.app.exception.ReservationConflictException;
import com.app.exception.RoomNotFoundException;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@XSlf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking with request: {}", request);

        // Fetch room and reservation
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationConflictException("Reservation not found"));

        // Log the fetched room and reservation details
        log.info("Fetched Room: Room ID = {}", room.getRoomId());
        log.info("Fetched Reservation: Reservation ID = {}", reservation.getReservationId());

        // Create and save the payment (this must happen before the booking)
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(request.getTotalPrice()); // Or another way to calculate it
        payment.setStatus(PaymentStatus.PENDING); // Or set based on your flow
        paymentRepository.save(payment);

        // Log before saving
        log.info("Saving payment: {}", payment);

        // Create and save the booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setReservation(reservation);
        booking.setBookingDate(request.getBookingDate());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setPayment(payment); // Associate the payment

        log.info("Saving booking: {}", booking);
        Booking savedBooking = bookingRepository.save(booking);

        // Log after saving
        log.info("Booking saved: {}", savedBooking);

        // Return the booking response
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
    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        log.info("Deleting booking with ID: {}", bookingId);

        // Fetch the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if payment exists and set its status (optional based on your business rules)
        Payment payment = booking.getPayment();
        if (payment != null) {
            payment.setStatus(PaymentStatus.CANCELED); // or any other status indicating cancellation
            paymentRepository.save(payment);
        }

        // Delete the booking
        bookingRepository.deleteById(bookingId);

        log.info("Booking with ID {} deleted successfully", bookingId);
    }
}

