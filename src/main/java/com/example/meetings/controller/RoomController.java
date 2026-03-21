package com.example.meetings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.meetings.dto.RoomRequest;
import com.example.meetings.model.Room;
import com.example.meetings.service.RoomService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> createRoom(
        @Valid @RequestBody RoomRequest request
    )
    {
        Room room = roomService.createRoom(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getRooms(
        @RequestParam(required = false) Integer minCapacity,
        @RequestParam(required = false) String amenity) {
            List<Room> rooms = roomService.getRooms(minCapacity, amenity);

            return ResponseEntity.ok(rooms);
        }
}
