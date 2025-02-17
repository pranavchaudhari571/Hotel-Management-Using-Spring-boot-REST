package com.app.dao;

import com.app.entities.Hotel;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findAll();
//    @Query("SELECT h FROM Hotel h JOIN FETCH h.addedBy WHERE h.id = :hotelId")
//    Hotel findByIdWithUser(@Param("hotelId") Long hotelId);



}

