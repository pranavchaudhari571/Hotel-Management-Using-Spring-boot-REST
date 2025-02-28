package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailability(boolean availability);
//    List<Room> findByHotel_HotelId(Long hotelId);
Optional<Room> findByRoomNumber(String roomNumber);
}