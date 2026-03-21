package com.example.meetings.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String organizerEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
