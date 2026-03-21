package com.example.meetings.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.meetings.model.Room;
import com.example.meetings.repository.RoomRepository;
import com.example.meetings.dto.BookingRequest;
import com.example.meetings.model.Booking;
import com.example.meetings.repository.BookingRepository;

import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepositiory;

    @Autowired
    private RoomRepository roomRepository;

    public Booking createBooking(BookingRequest request)
    {
        Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(() -> new RuntimeException("room not found"));

        if(!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Starttime must be before endtime");
        }

        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        if (minutes < 15 || minutes > 240)
        {
            throw new RuntimeException("Booking must be between 15 min and 4 hours");
        }

        DayOfWeek day = request.getStartTime().getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Bookings allowed only Mon-Fri");
        }

        LocalTime start = request.getStartTime().toLocalTime();
        LocalTime end = request.getEndTime().toLocalTime();

        if (start.isBefore(LocalTime.of(8, 0)) || end.isAfter(LocalTime.of(20, 0))) {
            throw new RuntimeException("Bookings allowed only between 08:00–20:00");
        }
        
        List<Booking> conflicts = bookingRepositiory.findConflictingBookings(request.getRoomId(), 
        request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Booking conflict exists");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setTitle(request.getTitle());
        booking.setOrganizerEmail(request.getOrganizerEmail());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("confirmed");

        return bookingRepositiory.save(booking);

    }


}
