package com.medicapp.server.doctors.controller;

import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/doctor")
@RequiredArgsConstructor
public class DoctorAdminController {

    private final DoctorService doctorService;
    @PostMapping
    public ResponseEntity<String> registerDoctor(@RequestBody DoctorRequest doctorRequest){
        doctorService.addDoctor(doctorRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Doctor Created Successfully.");
    }

    @PutMapping(path="{Doctor_id}")
    public ResponseEntity<String> updateDoctor(@PathVariable("Doctor_id") Integer Doctor_id,
                                                 @RequestBody DoctorRequest doctorRequest) {
        doctorService.updateDoctor(Doctor_id,doctorRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Doctor updated Successfully.");
    }

    @DeleteMapping(path="{Doctor_id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable("Doctor_id") Integer Doctor_id){
        doctorService.deleteDoctor(Doctor_id);
        return ResponseEntity.status(HttpStatus.OK).body("Doctor Deleted Successfully.");
    }

}
