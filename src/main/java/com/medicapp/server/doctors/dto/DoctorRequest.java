package com.medicapp.server.doctors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequest {
    private Integer user_id;
    private String specialty;
    private String description;
    private String details;
    private Float cost;
}
