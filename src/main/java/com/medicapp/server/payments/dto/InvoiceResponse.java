package com.medicapp.server.payments.dto;


import com.medicapp.server.appoinments.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private Integer id;
    private Float price;
    private String details;
    private LocalDateTime appointmentDate;
    private Integer userId;
    private String userName;
    private String userSurname;
    private String userEmail;
    private Integer doctorId;
    private String doctorName;
    private String doctorSurname;
    private String doctorSpecialization;
    private String doctorEmail;
}
