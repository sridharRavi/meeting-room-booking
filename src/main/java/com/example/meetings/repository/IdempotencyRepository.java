package com.example.meetings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.meetings.model.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByIdempotencyKeyAndOrganizerEmail(
            String idempotencyKey,
            String organizerEmail
    );
};
