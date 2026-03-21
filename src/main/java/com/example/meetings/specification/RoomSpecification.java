package com.example.meetings.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.meetings.model.Room;

public class RoomSpecification {
    public static Specification<Room> hasMinCapacity(Integer minCapacity) {
        return (root, query, cb) ->
            minCapacity == null ? null :
            cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
    }

    public static Specification<Room> hasAmenity(String amenity) {
        return (root, query, cb) ->
            amenity == null ? null :
            cb.isMember(amenity, root.get("amenities"));
    }
}
