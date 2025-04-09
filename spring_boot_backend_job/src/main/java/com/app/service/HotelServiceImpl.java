package com.app.service;

import java.util.List;

import java.util.stream.Collectors;

import com.app.dao.*;
import com.app.dto.*;
import com.app.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.exception.ReservationConflictException;
import com.app.exception.*;

import lombok.extern.slf4j.Slf4j;



@EnableCaching
@Slf4j
@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    private BookingService bookingService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PaymentRepository paymentRepository;


    @Autowired
    private ModelMapper modelMapper; // Inject ModelMapper


    @Override
    @Transactional
    @CachePut(value = "reservations", key = "#result.reservationId")

    public Reservation createReservation(CreateReservationRequest request, Long userId) {
        log.info("Creating reservation for guest: {}", request.getGuestName());

        // Validate if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate if the room exists
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    log.error("Room not found: {}", request.getRoomId());
                    return new RoomNotFoundException("Room not found");
                });


        // Check if room is available during the requested dates
//        Room room = roomRepository.findByRoomNumber(request.getRoomNumber())
//                .orElseThrow(() -> {
//                    log.error("Room not found: {}", request.getRoomNumber());
//                    return new RoomNotFoundException("Room not found");
//                });
        boolean roomIsBooked = reservationRepository.existsByRoomAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                room, request.getCheckOutDate(), request.getCheckInDate());

        if (roomIsBooked) {
            // Room is already booked during the requested dates
            throw new ReservationConflictException("Room is already booked during the requested dates.");
        }


        // Create the reservation
//        Reservation reservation = new Reservation();
//        reservation.setGuestName(request.getGuestName());
//        reservation.setUser(user);
//        reservation.setCheckInDate(request.getCheckInDate());
//        reservation.setCheckOutDate(request.getCheckOutDate());
//        reservation.setRoom(room);
//        reservation.setTotalPrice(request.getTotalPrice());
//        reservation.setEmail(request.getEmail());

        // After: Using ModelMapper
        Reservation reservation = modelMapper.map(request, Reservation.class);
        reservation.setUser(user);
        reservation.setRoom(room);
        // Set the default reservation status, as the new status field is required.
        reservation.setStatus(ReservationStatus.PENDING);
        // Process payment after saving the reservation to ensure reservationId is set
        // We are now processing the payment in the createBooking method
        log.info("Reservation created successfully for room: {}", room.getRoomId());

        // Send confirmation email
        // Send confirmation email with HTML body
        // Send confirmation email
        String emailBody = generateConfirmationEmail(request, room);

//        try {
//            notificationService.sendHtmlEmail(request.getEmail(), "Reservation Confirmation for" + request.getGuestName(), emailBody);
//        } catch (Exception e) {
//            log.error("Failed to send email: {}", e.getMessage());
//        }

        // Create an email message object in JSON format
        EmailMessage emailMessage = new EmailMessage(request.getEmail(),
                "Reservation Confirmation for " + request.getGuestName(), emailBody);

        try {
            String emailJson = new ObjectMapper().writeValueAsString(emailMessage);
            log.info("Email is sending: " + emailJson);
            kafkaTemplate.send("reservation-email-topic",emailJson);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize email message to JSON: {}", e.getMessage());
            // Optionally, rethrow the exception or handle it as needed
        }

        // Send the email message to Kafka
        // Save the reservation first to ensure reservationId is generated
        reservation = reservationRepository.save(reservation);

        room.setAvailability(false); // Mark the room as unavailable
        roomRepository.save(room);


        return reservation;
    }

    @Override
    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)

//	@Cacheable(value = "reservations")
    public Room createRoom(CreateRoomRequest roomRequest) {
        Long userId = getUserIdFromSecurityContext();

        // Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the hotel associated with the room
        Hotel hotel = hotelRepository.findById(roomRequest.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Create and save the room entity
        Room room = new Room();
        room.setRoomNumber(roomRequest.getRoomNumber());
        room.setType(roomRequest.getType());
        room.setPrice(roomRequest.getPrice());
        room.setAvailability(true);  // Default to true (room is available)
        room.setHotel(hotel);  // Set the hotel for the room
        room.setAddedBy(user);  // Set the user who added the room

        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully: {}", savedRoom.getRoomId());



        return savedRoom;
    }


    @Override
    @Transactional
    @CachePut(value = "reservations", key = "#result.reservationId")
    public Reservation updateReservation(UpdateReservationRequest request) {
        log.info("Updating reservation ID: {}", request.getReservationId());

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> {
                    log.error("Reservation not found: {}", request.getReservationId());
                    return new RuntimeException("Reservation not found");
                });

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    log.error("Room not found: {}", request.getRoomId());
                    return new RoomNotFoundException("Room not found");
                });
//
//        reservation.setGuestName(request.getGuestName());
//        reservation.setCheckInDate(request.getCheckInDate());
//        reservation.setCheckOutDate(request.getCheckOutDate());
//        reservation.setRoom(room);
//        reservation.setTotalPrice(request.getTotalPrice());
        modelMapper.map(request, reservation); // Map updated fields
        reservation.setRoom(room);

        log.info("Reservation updated successfully: {}", reservation.getRoom());
        return reservationRepository.save(reservation);
    }



    @Override
    @Transactional
    @CacheEvict(value = {"reservations", "rooms"}, key = "#reservationId")
    public void cancelReservation(Long reservationId) throws InterruptedException {
        log.info("Cancelling reservation ID: {}", reservationId);

        // Fetch the reservation to check if it exists
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.error("Reservation not found: {}", reservationId);
                    return new ReservationNotFoundException("Reservation not found");
                });


        List<BookingResponse> bookings = bookingService.getBookingsByReservation(reservation.getReservationId());
        if (bookings != null && !bookings.isEmpty()) {
            log.info("Deleting associated bookings for reservation ID: {}", reservationId);
            for (BookingResponse booking : bookings) {
                try {
                    bookingService.deleteBooking(booking.getId());
                    log.info("Booking ID {} deleted successfully", booking.getId());
                } catch (Exception e) {
                    log.error("Failed to delete booking ID: {}", booking.getId(), e);
                    throw new BookingDeletionException("Error while deleting booking record");
                }
            }
        }
        // Delete payment records first
        try {
            paymentRepository.deleteByReservation_ReservationId(reservationId);
            log.info("Payments for reservation ID: {} deleted", reservationId);
        } catch (Exception e) {
            log.error("Failed to delete payments for reservation ID: {}", reservationId);
            throw new PaymentDeletionException("Error while deleting payment records");
        }

        // Fetch and delete associated bookings
// we are doing soft delete of the reservation means maintaining logs and all but not deleting the reservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Revert room availability
        Room room = reservation.getRoom();
        room.setAvailability(true);
        roomRepository.save(room);

        // Delete the reservation last to avoid integrity violation
//        try {
//            reservationRepository.deleteById(reservationId);
//            reservationRepository.flush();
//            log.info("Reservation ID {} deleted successfully", reservationId);
//        } catch (Exception e) {
//            log.error("Failed to delete reservation ID: {}", reservationId);
//            throw new ReservationDeletionException("Error while deleting reservation");
//        }

        // Send cancellation email asynchronously via Kafka
        sendCancellationEmail(reservation);

        // Evict reservation from cache
        cacheManager.getCache("reservations").evict(reservationId);
        log.info("Reservation evicted from cache: {}", reservationId);

        log.info("Reservation canceled successfully: {}", reservationId);
    }


    private void sendCancellationEmail(Reservation reservation) {
        String emailBody = "<html><head><style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; padding: 20px; background-color: #f4f4f4; }"
                + "h2 { color: #F44336; }"
                + "h3 { color: #555; }"
                + "table { width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); }"
                + "th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }"
                + "th { background-color: #F44336; color: white; border-radius: 6px 6px 0 0; }"
                + "td { border-radius: 0 0 6px 6px; }"
                + ".button { display: inline-block; padding: 10px 20px; background-color: #F44336; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + ".footer { margin-top: 30px; font-size: 12px; color: #555; text-align: center; padding-top: 20px; border-top: 1px solid #ddd; }"
                + ".footer a { color: #F44336; text-decoration: none; }"
                + "</style></head><body>"
                + "<h2>Reservation Cancellation</h2>"
                + "<p>Dear <strong>" + reservation.getGuestName() + "</strong>,</p>"
                + "<p>We regret to inform you that your reservation has been canceled. Below are the details:</p>"
                + "<table>"
                + "<tr><th>Reservation ID</th><td>" + reservation.getReservationId() + "</td></tr>"
                + "<tr><th>Check-in Date</th><td>" + reservation.getCheckInDate() + "</td></tr>"
                + "<tr><th>Check-out Date</th><td>" + reservation.getCheckOutDate() + "</td></tr>"
                + "<tr><th>Room Type</th><td>" + reservation.getRoom().getType() + "</td></tr>"
                + "<tr><th>Total Price</th><td>₹" + reservation.getTotalPrice() + "</td></tr>"
                + "</table>"
                + "<p>If you have any questions or concerns, feel free to reach out to our <a href='https://www.goibibo.com/mysupport/trips/' class='button'>Customer Support</a>.</p>"
                + "<p>We apologize for any inconvenience caused.</p>"
                + "<p>Best regards,</p>"
                + "<p>PSC Bookings</p>"
                + "<div class='footer'><p>This is an automated message. Please do not reply directly to this email.</p></div>"
                + "</body></html>";

        EmailMessage emailMessage = new EmailMessage(reservation.getEmail(), "Reservation Canceled", emailBody);

        try {
            String emailJson = new ObjectMapper().writeValueAsString(emailMessage);
            log.info("Email is sending: {}", emailJson);
            kafkaTemplate.send("reservation-cancellation-topic", emailJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize email message to JSON: {}", e.getMessage());
            // Optionally, rethrow the exception or handle it as needed
        }
    }

    private String generateConfirmationEmail(CreateReservationRequest request, Room room) {
        return "<html><head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; padding: 20px; background-color: #f4f4f4; }"
                + "h2 { color: #4CAF50; }"
                + "h3 { color: #555; }"
                + "table { width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); }"
                + "th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }"
                + "th { background-color: #4CAF50; color: white; border-radius: 6px 6px 0 0; }"
                + "td { border-radius: 0 0 6px 6px; }"
                + ".button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + ".footer { margin-top: 30px; font-size: 12px; color: #555; text-align: center; padding-top: 20px; border-top: 1px solid #ddd; }"
                + ".footer a { color: #4CAF50; text-decoration: none; }"
                + "</style>"
                + "</head><body>"
                + "<h2>Reservation Confirmation</h2>"
                + "<p>Dear <strong>" + request.getGuestName() + "</strong>,</p>"
                + "<p>Your reservation has been confirmed. Here are the details:</p>"
                + "<table>"
                + "<tr><th>Check-in Date</th><td>" + request.getCheckInDate() + "</td></tr>"
                + "<tr><th>Check-out Date</th><td>" + request.getCheckOutDate() + "</td></tr>"
                + "<tr><th>Room Type</th><td>" + room.getType() + "</td></tr>"
                + "<tr><th>Total Price</th><td>₹" + request.getTotalPrice() + "</td></tr>"
                + "</table>"
                + "<p>We look forward to welcoming you to our hotel. If you have any questions, feel free to reach out to our <a href='https://www.goibibo.com/mysupport/trips/' class='button'>Customer Support</a>.</p>"
                + "<p>Best regards,</p>"
                + "<p>PSC Bookings </p>" // Fixed this part
                + "<div class='footer'><p>This is an automated message. Please do not reply directly to this email.</p></div>"
                + "</body></html>";
    }




    @Override
    @Transactional
    @Cacheable(value = "reservations")
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();


        return reservations;
    }

    @Override
    @Cacheable(value = "reservations", key = "#reservationId")
    @Transactional
    public Reservation getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Initialize lazy fields
        Hibernate.initialize(reservation.getUser());
        Hibernate.initialize(reservation.getRoom());
        Hibernate.initialize(reservation.getRoom().getHotel());

        return reservation;
    }



    @Override
    @Transactional
    public List<RoomResponse> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findByAvailability(true);
        log.info("Fetched available rooms: {}", availableRooms.size());
        return availableRooms.stream().map(room -> {
            String hotelName = room.getHotel() != null ? room.getHotel().getName() : "Unknown Hotel";
            RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);
            roomResponse.setHotelName(hotelName); // Set hotel name manually if needed
            return roomResponse;
        }).collect(Collectors.toList());


    }




    private Long getUserIdFromSecurityContext() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getAdminId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)

    public void updateRoom(UpdateRoomRequest request) {
        log.info("Updating room ID: {}", request.getRoomId());
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    log.error("Room not found: {}", request.getRoomId());
                    return new RuntimeException("Room not found");
                });

        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPrice(request.getPrice());
        room.setAvailability(request.isAvailability());

        roomRepository.save(room);
        log.info("Room updated successfully: {}", room.getRoomId());
    }

    @Override
    @Cacheable(value = "rooms")
    @Transactional
    public List<RoomResponse> getAllRooms() {
        log.info("Fetching all rooms");
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream().map(room -> {
            String hotelName = room.getHotel() != null ? room.getHotel().getName() : "Unknown Hotel";
            return new RoomResponse(
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getType(),
                    room.getPrice(),
                    room.isAvailability(),
                    hotelName
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Cacheable(value = "rooms", key = "#roomId")

    public RoomResponse getRoomById(Long roomId) {
        log.info("Fetching room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.error("Room not found: {}", roomId);
                    return new RuntimeException("Room not found");
                });

        String hotelName = room.getHotel() != null ? room.getHotel().getName() : "Unknown Hotel";

        return new RoomResponse(
                room.getRoomId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPrice(),
                room.isAvailability(),
                hotelName
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "rooms", key = "#roomId")

    public void deleteRoom(Long roomId) {
        log.info("Deleting room ID: {}", roomId);
        roomRepository.deleteById(roomId);
        log.info("Room deleted successfully: {}", roomId);
    }

    @Override
    @Transactional
    public List<BookedRoomResponse> getBookedRooms() {
        log.info("Fetching booked rooms");
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(reservation -> new BookedRoomResponse(reservation.getRoom().getRoomId(),
                        reservation.getRoom().getRoomNumber(), reservation.getRoom().getType(),
                        reservation.getCheckInDate(), reservation.getCheckOutDate()))
                .collect(Collectors.toList());
    }
}
