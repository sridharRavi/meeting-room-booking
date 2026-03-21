package com.example.meetings.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    private Long roomId;

    @NotBlank
    private String title;

    @Email
    private String organizerEmail;

    private LocalDateTime startTime;

    private LocalDateTime endTime;   
}
