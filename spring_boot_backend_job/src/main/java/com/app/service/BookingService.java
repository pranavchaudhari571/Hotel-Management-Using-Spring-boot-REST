package com.app.service;

import com.app.dto.BookingResponse;
import com.app.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request);
    List<BookingResponse> getBookingsByRoom(Long roomId);
    List<BookingResponse> getBookingsByReservation(Long reservationId);
    public void deleteBooking(Long bookingId);
    public default void Book(){
        System.out.println("this is method in interface");
    }
}

