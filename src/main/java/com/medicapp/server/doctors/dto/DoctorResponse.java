package com.medicapp.server.doctors.dto;

import com.medicapp.server.doctors.model.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Integer doctor_id;
    private String firstname;
    private String lastname;
    private String email;
    private String profile_url;
    private Specialty specialty;
    private String description;
    private String details;
    private Float rating;
    private Float cost;
}
