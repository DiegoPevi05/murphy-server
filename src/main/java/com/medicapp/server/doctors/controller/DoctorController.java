package com.medicapp.server.doctors.controller;



import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.dto.UserResponse;
import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/doctor/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public DoctorResponse getMyUser(){
        return doctorService.getMe();
    }
    @PutMapping(path="{Doctor_id}")
    public ResponseEntity<String> updateDoctor(@PathVariable("Doctor_id") Integer Doctor_id,
                                                 @RequestBody DoctorRequest doctorRequest) {
        doctorService.updateDoctorByDoctor(Doctor_id,doctorRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Doctor updaetd successfully.");
    }

    @PutMapping(value= "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateImage(
            @RequestPart("content_file_1") MultipartFile file_1) {

        doctorService.updateDoctorProfileImage(file_1);
        return ResponseEntity.status(HttpStatus.OK).body("Profile Image updated successfully.");
    }
}
