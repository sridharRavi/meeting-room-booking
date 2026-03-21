package com.example.meetings.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        if (ex.getMessage().contains("conflict")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
