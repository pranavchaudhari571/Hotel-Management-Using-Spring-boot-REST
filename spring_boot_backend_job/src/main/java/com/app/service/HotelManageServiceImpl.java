package com.app.service;

import com.app.dao.HotelRepository;
import com.app.dao.RoomRepository;
import com.app.dao.UserRepository;
import com.app.dto.HotelRequestDTO;
import com.app.dto.HotelResponseDTO;
import com.app.entities.Hotel;
import com.app.entities.Role;
import com.app.entities.User;
import com.app.exception.HotelNotfoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelManageServiceImpl implements HotelManageService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Add a new hotel
    @Transactional
    public HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO, Long adminId) {
        // Check if the admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UsernameNotFoundException("Only admins can add hotels");
        }

        // Create and save the hotel entity
        Hotel hotel = new Hotel();
        hotel.setName(hotelRequestDTO.getName());
        hotel.setLocation(hotelRequestDTO.getLocation());
        hotel.setAddedBy(admin);
        hotel.setPhoneNumber(hotelRequestDTO.getPhoneNumber());

        hotel = hotelRepository.save(hotel);

        // Map entity to response DTO
        return mapToResponseDTO(hotel);
    }

    // Update an existing hotel
    @Transactional
    public HotelResponseDTO updateHotel(Long hotelId, HotelRequestDTO hotelRequestDTO, Long adminId) {
        // Check if the admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UsernameNotFoundException("Only admins can update hotels");
        }

        // Find and update the hotel
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new UsernameNotFoundException("Hotel not found"));

        hotel.setName(hotelRequestDTO.getName());
        hotel.setLocation(hotelRequestDTO.getLocation());

        hotel = hotelRepository.save(hotel);

        return mapToResponseDTO(hotel);
    }

    // Get all hotels
    @Transactional
    public List<HotelResponseDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }


    // Get a hotel by ID
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotfoundException("Hotel not found"));
        return mapToResponseDTO(hotel);
    }

    // Delete a hotel
    @Transactional
    public void deleteHotel(Long hotelId, Long adminId) {
        // Check if the admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UsernameNotFoundException("Only admins can delete hotels");
        }

        // Check if the hotel exists
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotfoundException("Hotel not found"));

        // Delete the hotel
        hotelRepository.delete(hotel);
    }

    // Helper method to map Hotel entity to HotelResponseDTO
    private HotelResponseDTO mapToResponseDTO(Hotel hotel) {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setHotelId(hotel.getHotelId());
        dto.setName(hotel.getName());
        dto.setLocation(hotel.getLocation());
        dto.setAddedBy(hotel.getAddedBy().getUsername());
        dto.setPhoneNumber(hotel.getPhoneNumber());
        return dto;
    }
}

