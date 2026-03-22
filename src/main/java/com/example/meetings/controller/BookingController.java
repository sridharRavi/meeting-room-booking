package com.example.meetings.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meetings.dto.BookingRequest;
import com.example.meetings.model.Booking;
import com.example.meetings.service.BookingService;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
        @RequestBody BookingRequest request) {
            Booking booking = bookingService.createBooking(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        }

    @GetMapping
    public ResponseEntity<?> getBookings(
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) LocalDateTime from,
        @RequestParam(required = false) LocalDateTime to,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "0") int offset
    )
    {
        Map<String, Object> response = bookingService.getBookings(
            roomId, from, to, limit, offset);
        return ResponseEntity.ok(response);
    }
    
}
