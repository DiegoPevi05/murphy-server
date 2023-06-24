package com.medicapp.server.authentication.controller;

import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    @PutMapping(path="{User_id}")
    public ResponseEntity<String> updateUser(@PathVariable("User_id") Integer User_id,
                                             @RequestBody UserRequest userRequest) {
        userService.updateUser(User_id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully.");
    }
}