package com.example.meetings.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import com.example.meetings.model.Room;
import com.example.meetings.repository.RoomRepository;
import com.example.meetings.dto.BookingRequest;
import com.example.meetings.model.Booking;
import com.example.meetings.repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

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
        
        List<Booking> conflicts = bookingRepository.findConflictingBookings(request.getRoomId(), 
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

        return bookingRepository.save(booking);

    }

    public Map<String, Object> getBookings(
        Long roomId,
        LocalDateTime from,
        LocalDateTime to,
        int limit,
        int offset )
    {

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Specification<Booking> spec =  (root, query, cb) -> cb.conjunction();
        
        if (roomId != null) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("room").get("id"), roomId));
    }

    if (from != null) {
        spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startTime"), from));
    }

    if (to != null) {
        spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("endTime"), to));
    }
        
    Page<Booking> page = bookingRepository.findAll(spec, pageable);

    Map<String, Object> response = new HashMap<>();
    response.put("items", page.getContent());
    response.put("total", page.getTotalElements());
    response.put("limit", limit);
    response.put("offset", offset);

    return response;
    }
}
