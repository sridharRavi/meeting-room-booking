package com.example.meetings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meetings.dto.BookingRequest;
import com.example.meetings.model.Booking;
import com.example.meetings.service.BookingService;

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
    
}
