package com.medicapp.server.authentication.controller;

import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.service.UserService;
import com.medicapp.server.doctors.dto.DoctorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest){
        userService.addUser(registerRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User Created Successfully.");
    }

    @PutMapping(path="{User_id}")
    public ResponseEntity<String> updateUser(@PathVariable("User_id") Integer User_id,
                                             @RequestBody UserRequest userRequest) {
        userService.updateUser(User_id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully.");
    }

    @DeleteMapping(path="{User_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("User_id") Integer User_id){
        userService.deleteUser(User_id);
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted Successfully.");
    }
}