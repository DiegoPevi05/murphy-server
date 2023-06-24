package com.medicapp.server.doctors.controller;


import com.medicapp.server.doctors.dto.TimeSheetResponse;
import com.medicapp.server.doctors.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/timesheets")
@RequiredArgsConstructor
public class TimeSheetController {
    private final TimeSheetService timeSheetService;
    @GetMapping
    public List<TimeSheetResponse> getTimesheets(
            @RequestParam(value = "date" , defaultValue = "") String date,
            @RequestParam(value = "week", defaultValue = "true") String week,
            @RequestParam(value = "doctor_id", defaultValue = "0") int doctor_id
    ){
        LocalDateTime dateTime = null;
        boolean isWeek = Boolean.parseBoolean(week);
        if (!date.isEmpty()) {
            dateTime = LocalDateTime.parse(date);
        }
        return timeSheetService.getTimeSheets(dateTime, isWeek, doctor_id);
    }
}
