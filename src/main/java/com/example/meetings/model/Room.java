package com.example.meetings.model;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


@Entity
@Table(name="rooms",
    uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private int capacity;
    private int floor;

    @ElementCollection
    private List<String> amenities;

}
