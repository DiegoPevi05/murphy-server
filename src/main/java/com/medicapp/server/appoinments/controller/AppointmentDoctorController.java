package com.medicapp.server.appoinments.controller;

import com.medicapp.server.appoinments.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor/appointment")
@RequiredArgsConstructor
public class AppointmentDoctorController {
    private final AppointmentService appointmentService;

    @DeleteMapping(path="{appointment_id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable("appointment_id") Integer Appointment_id){
        appointmentService.deleteAppointmentDoctor(Appointment_id);
        return ResponseEntity.status(HttpStatus.OK).body("Meet Deleted Successfully.");
    }
}
