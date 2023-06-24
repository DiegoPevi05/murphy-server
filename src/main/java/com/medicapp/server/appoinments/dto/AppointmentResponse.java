package com.medicapp.server.appoinments.dto;

import com.medicapp.server.doctors.model.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {

    private Integer id;
    private Integer doctor_id;
    private String doctor_firstname;
    private String doctor_specialty;
    private String doctor_email;
    private String doctor_profile_url;
    private String doctor_description;
    private Float doctor_cost;
    private Integer user_id;
    private String user_firstname;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
