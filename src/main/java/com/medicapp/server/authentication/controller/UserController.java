package com.medicapp.server.authentication.controller;

import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.dto.UserResponse;
import com.medicapp.server.authentication.service.UserService;
import com.medicapp.server.doctors.dto.DoctorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/v1/user/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public UserResponse getMyUser(){
        return userService.getMe();
    }
    @PutMapping(path="{User_id}")
    public ResponseEntity<String> updateUser(@PathVariable("User_id") Integer User_id,
                                             @RequestBody UserRequest userRequest) {
        userService.updateUserByUser(User_id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully.");
    }

    @PutMapping(value= "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateImage(
            @RequestPart("content_file_1") MultipartFile file_1) {

        userService.updateUserProfileImage(file_1);
        return ResponseEntity.status(HttpStatus.OK).body("Profile Image updated successfully.");
    }
}