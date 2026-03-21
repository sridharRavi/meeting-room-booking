package com.example.meetings.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {
    @NotBlank
    private String name;

    @Min(1)
    private int capacity;

    private int floor;

    private List<String> amenities;
}
