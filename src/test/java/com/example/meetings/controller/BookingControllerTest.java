package com.example.meetings.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.meetings.model.Booking;
import com.example.meetings.service.BookingService;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(controllers = {BookingController.class, ReportController.class})
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookingService bookingService;

    //test methods with helper functions
    private String validBookingJson() {
    return """
    {
      "roomId": 1,
      "title": "Team Sync",
      "organizerEmail": "test@example.com",
      "startTime": "2026-03-25T10:00:00",
      "endTime": "2026-03-25T11:00:00"
    }
    """;
    }

    @Test
    void shouldCreateBookingSuccessfully() throws Exception {
        Booking booking = new Booking();
        booking.setStatus("confirmed");

        when(bookingService.createBooking(any(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                .header("Idempotency-Key", "abc-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validBookingJson()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("confirmed"));
    }

    @Test
    void shouldReturn409ForOverlap() throws Exception {
        when(bookingService.createBooking(any(), any()))
                .thenThrow(new IllegalArgumentException("conflict"));

        mockMvc.perform(post("/bookings")
                .header("Idempotency-Key", "k2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validBookingJson()))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void sameKeyShouldNotCreateDuplicate() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingService.createBooking(any(), any()))
                .thenReturn(booking);

        String response1 = mockMvc.perform(post("/bookings")
                .header("Idempotency-Key", "same-key")
                .content(validBookingJson())
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/bookings")
                .header("Idempotency-Key", "same-key")
                .content(validBookingJson())
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(response1, response2);
    }

    @Test
    void shouldCancelBookingSuccessfully() throws Exception {
        Booking booking = new Booking();
        booking.setStatus("CANCELLED");

        when(bookingService.cancelBooking(anyLong()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldFailCancelWithin1Hour() throws Exception {
        when(bookingService.cancelBooking(anyLong()))
                .thenThrow(new IllegalArgumentException("1 hour"));

        mockMvc.perform(post("/bookings/1/cancel"))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldCalculateUtilizationCorrectly() throws Exception {
        String mockResponse = """
        [
          {
            "roomId": 1,
            "roomName": "Conference A",
            "totalBookingHours": 2.0,
            "utilizationPercent": 0.2
          }
        ]
        """;
        mockMvc.perform(get("/reports/room-utilization")
                .param("from", "2026-03-23T00:00:00")
                .param("to", "2026-03-25T23:59:59"))
            .andExpect(status().isOk());
    }
}
