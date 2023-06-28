package com.medicapp.server.prescriptions.controller;

import com.medicapp.server.prescriptions.dto.PrescriptionResponse;
import com.medicapp.server.prescriptions.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/prescription")
@RequiredArgsConstructor
public class PrescriptionUserController {

    private final PrescriptionService prescriptionService;
    @GetMapping
    public Page<PrescriptionResponse> getPrescriptions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "appointment_id", defaultValue = "0") int appointment_id
    ){
        return prescriptionService.getPrescriptionsByUser(page,size,appointment_id);
    }
}
