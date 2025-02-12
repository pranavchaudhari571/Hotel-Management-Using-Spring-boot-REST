package com.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.app.dao.HotelRepository;
import com.app.dao.UserRepository;
import com.app.entities.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.ReservationRepository;
import com.app.dao.RoomRepository;
import com.app.dto.BookedRoomResponse;
import com.app.dto.CreateReservationRequest;
import com.app.dto.CreateRoomRequest;
import com.app.dto.RoomResponse;
import com.app.dto.UpdateReservationRequest;
import com.app.dto.UpdateRoomRequest;
import com.app.exception.ReservationConflictException;
import com.app.exception.RoomNotFoundException;

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

		// Check if room is available
		if (!room.isAvailability()) {
			throw new ReservationConflictException("Room is already booked.");
		}

		// Create the reservation
		Reservation reservation = new Reservation();
		reservation.setGuestName(request.getGuestName());
		reservation.setUser(user);
		reservation.setCheckInDate(request.getCheckInDate());
		reservation.setCheckOutDate(request.getCheckOutDate());
		reservation.setRoom(room);
		reservation.setTotalPrice(request.getTotalPrice());
		reservation.setEmail(request.getEmail());


		room.setAvailability(false);
		roomRepository.save(room);

		// Process payment
		paymentService.processPayment(reservation);

		log.info("Reservation created successfully for room: {}", room.getRoomId());

		// Send confirmation email
		String emailBody = "Dear " + request.getGuestName() + ", your reservation is confirmed!";
		try {
			notificationService.sendEmail(request.getEmail(), "Reservation Confirmation", emailBody);
		} catch (Exception e) {
			log.error("Failed to send email: {}", e.getMessage());
		}

		return reservationRepository.save(reservation);
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

		reservation.setGuestName(request.getGuestName());
		reservation.setCheckInDate(request.getCheckInDate());
		reservation.setCheckOutDate(request.getCheckOutDate());
		reservation.setRoom(room);
		reservation.setTotalPrice(request.getTotalPrice());

		log.info("Reservation updated successfully: {}", reservation.getRoom());
		return reservationRepository.save(reservation);
	}

	@Override
	public List<RoomResponse> getAvailableRooms() {
		List<Room> availableRooms = roomRepository.findByAvailability(true);
		log.info("Fetched available rooms: {}", availableRooms.size());

		return availableRooms.stream().map(room -> {
			String hotelName = room.getHotel() != null ? room.getHotel().getName() : "Unknown Hotel";
			return new RoomResponse(
					room.getRoomId(),
					room.getRoomNumber(),
					room.getType(),
					room.getPrice(),
					room.isAvailability(),
					hotelName // Add hotel name
			);
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	@CacheEvict(value = {"reservations", "rooms"}, allEntries = true)
	public void cancelReservation(Long reservationId) {
		log.info("Cancelling reservation ID: {}", reservationId);
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> {
					log.error("Reservation not found: {}", reservationId);
					return new RuntimeException("Reservation not found");
				});

		Room room = reservation.getRoom();
		room.setAvailability(true);
		roomRepository.save(room);

		reservationRepository.deleteById(reservationId);
		log.info("Reservation cancelled successfully: {}", reservationId);

		// Send cancellation email
		String emailBody = "Dear " + reservation.getGuestName() + ", your reservation has been canceled.";
		notificationService.sendEmail(reservation.getEmail(), "Reservation Canceled", emailBody);
	}

	private void validateReservation(CreateReservationRequest request) {
		Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new RoomNotFoundException("Room not found"));

		// Check for date overlap
		boolean isConflict = reservationRepository
				.existsByRoomAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(room, request.getCheckOutDate(),
						request.getCheckInDate());

		if (isConflict) {
			log.error("Conflict: Reservation already exists for the selected room and dates: {} to {}",
					request.getCheckInDate(), request.getCheckOutDate());
			throw new ReservationConflictException("Conflict: Room is already booked for the selected dates.");
		}
	}

	@Override
	@Transactional
	public List<Reservation> getAllReservations() {
		List<Reservation> reservations = reservationRepository.findAll();

		reservations.forEach(reservation -> {
			Hibernate.initialize(reservation.getUser());
			Hibernate.initialize(reservation.getRoom());
			Hibernate.initialize(reservation.getRoom().getHotel());
		});

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
	@CacheEvict(value = "rooms", allEntries = true)
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

		if (savedRoom == null || savedRoom.getRoomId() == null) {
			log.error("Room was not saved correctly. Room: {}", savedRoom);
			throw new RuntimeException("Failed to create room, roomId is null.");
		}

		return savedRoom;
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
