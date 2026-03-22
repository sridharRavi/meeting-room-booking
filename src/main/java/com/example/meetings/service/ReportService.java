package com.example.meetings.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.meetings.dto.RoomUtilizationResponse;
import com.example.meetings.model.Booking;
import com.example.meetings.model.Room;
import com.example.meetings.repository.BookingRepository;
import com.example.meetings.repository.RoomRepository;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {
     @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<RoomUtilizationResponse> getRoomUtilization(
            LocalDateTime from,
            LocalDateTime to
    ) {

        List<Booking> bookings = bookingRepository.findBookingsInRange(from, to);
        List<Room> rooms = roomRepository.findAll();

        // Group bookings by roomId
        Map<Long, List<Booking>> bookingsByRoom = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getRoom().getId()));

        List<RoomUtilizationResponse> result = new ArrayList<>();

        double totalBusinessHours = calculateBusinessHours(from, to);

        for (Room room : rooms) {

            List<Booking> roomBookings = bookingsByRoom.getOrDefault(room.getId(), List.of());

            double totalBookingHours = 0.0;

            for (Booking b : roomBookings) {

                LocalDateTime overlapStart = max(b.getStartTime(), from);
                LocalDateTime overlapEnd = min(b.getEndTime(), to);

                double hours = Duration.between(overlapStart, overlapEnd).toMinutes() / 60.0;
                totalBookingHours += hours;
            }

            double utilization = totalBusinessHours == 0
                    ? 0
                    : totalBookingHours / totalBusinessHours;

            result.add(new RoomUtilizationResponse(
                    room.getId(),
                    room.getName(),
                    totalBookingHours,
                    utilization
            ));
        }

        return result;   
    }

    private double calculateBusinessHours(LocalDateTime from, LocalDateTime to) {

    double totalHours = 0.0;

    LocalDate currentDate = from.toLocalDate();
    LocalDate endDate = to.toLocalDate();

    while (!currentDate.isAfter(endDate)) {

        DayOfWeek day = currentDate.getDayOfWeek();

        if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {

            LocalDateTime dayStart = currentDate.atTime(8, 0);
            LocalDateTime dayEnd = currentDate.atTime(20, 0);

            LocalDateTime overlapStart = max(dayStart, from);
            LocalDateTime overlapEnd = min(dayEnd, to);

            if (overlapStart.isBefore(overlapEnd)) {
                double hours = Duration.between(overlapStart, overlapEnd).toMinutes() / 60.0;
                totalHours += hours;
            }
        }

        currentDate = currentDate.plusDays(1);
    }
    return totalHours;
    }

    private LocalDateTime max(LocalDateTime a, LocalDateTime b) {
    return a.isAfter(b) ? a : b;
    }

    private LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }
}
