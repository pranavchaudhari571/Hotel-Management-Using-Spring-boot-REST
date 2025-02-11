package com.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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
import com.app.entities.Reservation;
import com.app.entities.Room;
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
	private NotificationService notificationService;

	@Override
	@Transactional
	@CachePut(value = "reservations", key = "#result.reservationId")
	public Reservation createReservation(CreateReservationRequest request) {
		log.info("Creating reservation for guest: {}", request.getGuestName());
		validateReservation(request);

		Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> {
			log.error("Room not found: {}", request.getRoomId());
			return new RoomNotFoundException("Room not found");
		});

		Reservation reservation = new Reservation();
		reservation.setGuestName(request.getGuestName());
		reservation.setCheckInDate(request.getCheckInDate());
		reservation.setCheckOutDate(request.getCheckOutDate());
		reservation.setRoom(room);
		reservation.setTotalPrice(request.getTotalPrice());
		reservation.setEmail(request.getEmail());

		room.setAvailability(false);
		roomRepository.save(room);

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
		Reservation reservation = reservationRepository.findById(request.getReservationId()).orElseThrow(() -> {
			log.error("Reservation not found: {}", request.getReservationId());
			return new RuntimeException("Reservation not found");
		});

		Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> {
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
		return availableRooms.stream().map(room -> new RoomResponse(room.getRoomId(), room.getRoomNumber(),
				room.getType(), room.getPrice(), room.isAvailability())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	@CacheEvict(value = {"reservations", "rooms"}, allEntries = true)
	public void cancelReservation(Long reservationId) {
		log.info("Cancelling reservation ID: {}", reservationId);
		Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> {
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
	@Cacheable(value = "reservations")
	public List<Reservation> getAllReservations() {
		log.info("Fetching all reservations");
		return reservationRepository.findAll();
	}

	@Override
	@Cacheable(value = "reservations", key = "#reservationId")
	public Reservation getReservationById(Long reservationId) {
		log.info("Fetching reservation ID: {}", reservationId);
		return reservationRepository.findById(reservationId).orElseThrow(() -> {
			log.error("Reservation not found: {}", reservationId);
			return new RuntimeException("Reservation not found");
		});
	}

	@Override
	@Transactional
	@CacheEvict(value = "rooms", allEntries = true)
	public Room createRoom(CreateRoomRequest request) {
		log.info("Creating room with number: {}", request.getRoomNumber());
		Room room = new Room();
		room.setRoomNumber(request.getRoomNumber());
		room.setType(request.getType());
		room.setPrice(request.getPrice());
		room.setAvailability(true);

		Room savedRoom = roomRepository.save(room); // Ensure the room is saved before accessing its ID
		log.info("Room created successfully: {}", savedRoom.getRoomId());
		if (savedRoom == null || savedRoom.getRoomId() == null) {
			log.error("Room was not saved correctly. Room: {}", savedRoom);
			throw new RuntimeException("Failed to create room, roomId is null.");
		}

		return savedRoom; // Return the saved room
	}

	@Override
	@Transactional
	@CacheEvict(value = "rooms", allEntries = true)
	public void updateRoom(UpdateRoomRequest request) {
		log.info("Updating room ID: {}", request.getRoomId());
		Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> {
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
		return rooms.stream().map(room -> new RoomResponse(room.getRoomId(), room.getRoomNumber(), room.getType(),
				room.getPrice(), room.isAvailability())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	@Cacheable(value = "rooms", key = "#roomId")
	public RoomResponse getRoomById(Long roomId) {
		log.info("Fetching room ID: {}", roomId);
		Room room = roomRepository.findById(roomId).orElseThrow(() -> {
			log.error("Room not found: {}", roomId);
			return new RuntimeException("Room not found");
		});
		return new RoomResponse(room.getRoomId(), room.getRoomNumber(), room.getType(), room.getPrice(),
				room.isAvailability());
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
