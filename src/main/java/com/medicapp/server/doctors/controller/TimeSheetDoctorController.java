package com.medicapp.server.doctors.controller;

import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.dto.TimeSheetRequest;
import com.medicapp.server.doctors.dto.TimeSheetResponse;
import com.medicapp.server.doctors.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor/timesheets")
@RequiredArgsConstructor
public class TimeSheetDoctorController {
    private final TimeSheetService timeSheetService;

    @GetMapping
    public List<TimeSheetResponse> getTimesheets(
            @RequestParam(value = "date" , defaultValue = "") String date
    ){
        LocalDateTime dateTime = null;
        if (!date.isEmpty()) {
            dateTime = LocalDateTime.parse(date);
        }
        return timeSheetService.getTimeSheetsDoctor(dateTime);
    }
    @PostMapping
    public ResponseEntity<String> registerTimeSheet(@RequestBody TimeSheetRequest timeSheetRequest){
        timeSheetService.addTimeSheetsByDoctor(timeSheetRequest);
        return ResponseEntity.status(HttpStatus.OK).body("TimeSheet created Successfully.");
    }

    @DeleteMapping(path="{TimesheetId}")
    public ResponseEntity<String> deleteTimeSheet(@PathVariable("TimesheetId") Integer TimeSheetId,
                                                  @RequestParam(value="doctor_id",defaultValue = "") Integer doctor_id){
        timeSheetService.deleteTimeSheetsByDoctor(TimeSheetId,doctor_id);
        return ResponseEntity.status(HttpStatus.OK).body("TimeSheet deleted Successfully.");
    }
}
