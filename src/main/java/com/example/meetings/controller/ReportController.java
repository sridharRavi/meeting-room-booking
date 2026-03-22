package com.example.meetings.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.meetings.dto.RoomUtilizationResponse;
import com.example.meetings.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/room-utilization")
    public List<RoomUtilizationResponse> getRoomUtilization(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    ) {
        return reportService.getRoomUtilization(from, to);
    }
}
