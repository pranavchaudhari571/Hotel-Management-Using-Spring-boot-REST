package com.app.service;

import com.app.dto.HotelRequestDTO;
import com.app.dto.HotelResponseDTO;

import java.util.List;

public interface HotelManageService {
    public HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO, Long adminId);
    public HotelResponseDTO updateHotel(Long hotelId, HotelRequestDTO hotelRequestDTO, Long adminId);
    public List<HotelResponseDTO> getAllHotels();
    public HotelResponseDTO getHotelById(Long hotelId);
    public void deleteHotel(Long hotelId, Long adminId);



}
