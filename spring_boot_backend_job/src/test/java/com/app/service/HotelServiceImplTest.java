package com.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dao.ReservationRepository;
import com.app.dao.RoomRepository;
import com.app.dto.CreateReservationRequest;
import com.app.dto.RoomResponse;
import com.app.dto.UpdateReservationRequest;
import com.app.entities.Reservation;
import com.app.entities.Room;
import com.app.exception.RoomNotFoundException;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Room room;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
    	String priceString = "100.00"; // Example String price
    	BigDecimal price = new BigDecimal(priceString); // Convert String to BigDecimal
        room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setType("Single");
        room.setPrice(price);
        room.setAvailability(true);

        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setGuestName("Pranav Chaudhari");
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(2));
        reservation.setTotalPrice(price);
        reservation.setEmail("john@example.com");
    }

    @Test
    void createReservation_Success() {
    	String priceString = "200.00"; // Example String price
    	BigDecimal price = new BigDecimal(priceString); // Convert String to BigDecimal
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(1L);
        request.setGuestName("Pranav Chaudhari");
        request.setCheckInDate(LocalDate.now());
        request.setCheckOutDate(LocalDate.now().plusDays(2));
        request.setTotalPrice(price);
        request.setEmail("john@example.com");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        doNothing().when(notificationService).sendEmail(anyString(), anyString(), anyString());

        Reservation createdReservation = hotelService.createReservation(request);

        assertNotNull(createdReservation);
        assertEquals("Pranav Chaudhari", createdReservation.getGuestName());
        assertFalse(room.isAvailability()); // Room should be marked unavailable
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_RoomNotFound() {
    	String priceString = "200.00"; // Example String price
    	BigDecimal price = new BigDecimal(priceString); // Convert String to BigDecimal
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(1L);
        request.setGuestName("Pranav Chaudhari");
        request.setCheckInDate(LocalDate.now());
        request.setCheckOutDate(LocalDate.now().plusDays(2));
        request.setTotalPrice(price);
        request.setEmail("john@example.com");

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoomNotFoundException.class, () -> hotelService.createReservation(request));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void updateReservation_Success() {
    	String priceString = "300"; // Example String price
    	BigDecimal price = new BigDecimal(priceString); // Convert String to BigDecimal
        UpdateReservationRequest request = new UpdateReservationRequest();
        request.setReservationId(1L);
        request.setRoomId(1L);
        request.setGuestName("Pranav Chaudhari Updated");
        request.setCheckInDate(LocalDate.now());
        request.setCheckOutDate(LocalDate.now().plusDays(3));
        request.setTotalPrice(price);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation updatedReservation = hotelService.updateReservation(request);

        assertEquals("Pranav Chaudhari Updated", updatedReservation.getGuestName());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    @Test
    void cancelReservation_Success() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        hotelService.cancelReservation(1L);

        // Assert
        assertTrue(room.isAvailability()); // Room should now be marked available
        verify(reservationRepository, times(1)).deleteById(1L); // Ensure the reservation was deleted
        verify(roomRepository, times(1)).save(room); // Ensure the room state was saved
    }


    @Test
    void cancelReservation_ReservationNotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> hotelService.cancelReservation(1L));
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    void getAvailableRooms() {
        when(roomRepository.findByAvailability(true)).thenReturn(List.of(room));

        List<RoomResponse> availableRooms = hotelService.getAvailableRooms();

        assertEquals(1, availableRooms.size());
        assertEquals("101", availableRooms.get(0).getRoomNumber());
    }

    @Test
    void getAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Reservation> reservations = hotelService.getAllReservations();

        assertEquals(1, reservations.size());
        assertEquals("Pranav Chaudhari", reservations.get(0).getGuestName());
    }

    @Test
    void getReservationById_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation fetchedReservation = hotelService.getReservationById(1L);

        assertEquals("Pranav Chaudhari", fetchedReservation.getGuestName());
    }

    @Test
    void getReservationById_NotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> hotelService.getReservationById(1L));
        assertEquals("Reservation not found", exception.getMessage());
    }

    // Add more tests for createRoom, updateRoom, deleteRoom, and other methods as needed
}
