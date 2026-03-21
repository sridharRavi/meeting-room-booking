package com.example.meetings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.meetings.dto.RoomRequest;
import com.example.meetings.model.Room;
import com.example.meetings.repository.RoomRepository;
import com.example.meetings.specification.RoomSpecification;

import java.util.List;

@Service
public class RoomService {
     @Autowired
    private RoomRepository roomRepository;

    public Room createRoom(RoomRequest request) {

        roomRepository.findByNameIgnoreCase(request.getName())
            .ifPresent(r -> {
                throw new RuntimeException("Room name already exists");
            });

        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setFloor(request.getFloor());
        room.setAmenities(request.getAmenities());

        return roomRepository.save(room);
    }

    public List<Room> getRooms(Integer minCapacity, String amenity)
    {
        Specification<Room> spec = (root, query, cb) -> cb.conjunction();
        if(minCapacity != null)
        {
            spec = spec.and(RoomSpecification.hasMinCapacity(minCapacity));
        }

        if (amenity!=null)
        {
            spec=spec.and(RoomSpecification.hasAmenity(amenity));
        }

        return roomRepository.findAll(spec);
    }
}
