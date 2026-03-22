package com.example.meetings.repository;

import java.time.LocalDateTime;

import com.example.meetings.model.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> 
{
    @Query("""
            SELECT  b from Booking b
            WHERE b.room.id = :roomId
            AND b.startTime  < :endTime
            AND b.endTime > :startTime
            """)
        List<Booking> findConflictingBookings(
            @Param("roomId") Long roomId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
}
