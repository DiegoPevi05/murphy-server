package com.medicapp.server.doctors.controller;

import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/doctor")
@RequiredArgsConstructor
public class DoctorUserController {
    private final DoctorService doctorService;
    @GetMapping
    public Page<DoctorResponse> getDoctors(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "specialty", defaultValue = "") String specialty
    ){
        return doctorService.getDoctors(page,size,name,specialty);
    }
}