package com.example.meetings.dto;

import lombok.Getter;

@Getter
public class RoomUtilizationResponse {
    private Long roomId;
    private String roomName;
    private double totalBookingHours;
    private double utilizationPercent;

    // constructor
    public RoomUtilizationResponse(Long roomId, String roomName,
                                   double totalBookingHours, double utilizationPercent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalBookingHours = totalBookingHours;
        this.utilizationPercent = utilizationPercent;
    }
}
