package com.medicapp.server.appoinments.controller;

import com.medicapp.server.appoinments.dto.AppointmentRequest;
import com.medicapp.server.appoinments.dto.AppointmentResponse;
import com.medicapp.server.appoinments.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/user/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    @GetMapping
    public Page<AppointmentResponse> getAppointments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "") String date,
            @RequestParam(value = "month", defaultValue = "false") String month,
            @RequestParam(value = "specialty", defaultValue = "") String specialty
    ){
        LocalDateTime dateTime = null;
        boolean isMonth = Boolean.parseBoolean(month);
        if (!date.isEmpty()) {
            dateTime = LocalDateTime.parse(date);
        }
        return appointmentService.getAppointmentsUser(page,size,dateTime,isMonth,specialty);
    }

    @PostMapping
    public ResponseEntity<String> registerAppointment(@RequestBody AppointmentRequest appointmentRequest){
        appointmentService.addAppointmentUser(appointmentRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Meet Created Successfully.");
    }

    @DeleteMapping(path="{appointment_id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable("appointment_id") Integer Appointment_id){
        appointmentService.deleteAppointmentUser(Appointment_id);
        return ResponseEntity.status(HttpStatus.OK).body("Meet Deleted Successfully.");
    }
}
