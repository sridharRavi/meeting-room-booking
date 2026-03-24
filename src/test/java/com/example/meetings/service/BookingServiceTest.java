package com.example.meetings.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.lenient;

import com.example.meetings.dto.BookingRequest;
import com.example.meetings.model.Booking;
import com.example.meetings.repository.BookingRepository;
import com.example.meetings.repository.IdempotencyRepository;
import com.example.meetings.repository.RoomRepository;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        lenient().when(idempotencyRepository
            .findByIdempotencyKeyAndOrganizerEmail(any(), any()))
            .thenReturn(Optional.empty());
    }

    @Test
    void shouldFailIfDurationTooShort() {
        BookingRequest req = new BookingRequest();
        req.setStartTime(LocalDateTime.now().plusHours(1));
        req.setEndTime(LocalDateTime.now().plusHours(1).plusMinutes(10));
        req.setOrganizerEmail("test@example.com");
        req.setRoomId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(new com.example.meetings.model.Room()));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            bookingService.createBooking(req, "key1")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("15 min"));
    }

    @Test
    void shouldFailOutsideWorkingHours() {
        BookingRequest req = new BookingRequest();
        req.setStartTime(LocalDateTime.of(2026, 3, 22, 7, 0));
        req.setEndTime(LocalDateTime.of(2026, 3, 22, 9, 0));
        req.setOrganizerEmail("test@example.com");
        req.setRoomId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(new com.example.meetings.model.Room()));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            bookingService.createBooking(req, "key2")
        );

        assertTrue(ex.getMessage().contains("Mon-Fri"));
    }

    @Test
    void shouldFailIfOverlapExists() {
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
        .thenReturn(List.of(new Booking()));

        when(roomRepository.findById(1L))
            .thenReturn(Optional.of(new com.example.meetings.model.Room()));

        BookingRequest req = new BookingRequest();
        req.setStartTime(LocalDateTime.now().plusHours(2));
        req.setEndTime(LocalDateTime.now().plusHours(3));
        req.setOrganizerEmail("test@example.com");
        req.setRoomId(1L);

        Exception ex = assertThrows(RuntimeException.class, () ->
            bookingService.createBooking(req, "key3")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("conflict"));
    }

    @Test
    void shouldFailIfCancelWithin1Hour() {
        Booking booking = new Booking();
        booking.setStartTime(LocalDateTime.now().plusMinutes(30));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Exception ex = assertThrows(RuntimeException.class, () ->
            bookingService.cancelBooking(1L)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("1 hour"));
    }


}
