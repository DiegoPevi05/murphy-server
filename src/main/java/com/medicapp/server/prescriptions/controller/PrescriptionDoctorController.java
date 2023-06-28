package com.medicapp.server.prescriptions.controller;

import com.medicapp.server.prescriptions.dto.PrescriptionResponse;
import com.medicapp.server.prescriptions.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/doctor/prescription")
@RequiredArgsConstructor
public class PrescriptionDoctorController {
    private final PrescriptionService prescriptionService;
    @GetMapping
    public Page<PrescriptionResponse> getPrescriptions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "appointment_id", defaultValue = "0") int appointment_id
    ){
        return prescriptionService.getPrescriptionByDoctor(page,size,appointment_id);
    }

    @PostMapping
    public ResponseEntity<String> registerPrescription(@RequestPart("appointment_id") Integer appointment_id,
                                                       @RequestPart("prescription_content") String prescription_content,
                                                       @RequestPart("content_file_1") MultipartFile file_1){
        prescriptionService.addPrescriptionByDoctor(appointment_id,prescription_content,file_1);
        return ResponseEntity.status(HttpStatus.OK).body("Prescription Created Successfully.");
    }

}
