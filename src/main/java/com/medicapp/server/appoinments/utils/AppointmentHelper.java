package com.medicapp.server.appoinments.utils;

import com.medicapp.server.appoinments.dto.AppointmentResponse;
import com.medicapp.server.appoinments.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentHelper {
    public AppointmentResponse mapToAppoinmentResponse(Appointment appointment){
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctor_id(appointment.getDoctor().getId())
                .doctor_firstname(appointment.getDoctor().getUser().getFirstname())
                .doctor_specialty(appointment.getDoctor().getSpecialty().name())
                .doctor_email(appointment.getDoctor().getUser().getEmail())
                .doctor_profile_url(appointment.getDoctor().getUser().getProfile_url())
                .doctor_description(appointment.getDoctor().getDescription())
                .doctor_cost(appointment.getDoctor().getCost())
                .user_id(appointment.getUser().getId())
                .user_firstname(appointment.getUser().getFirstname())
                .date(appointment.getDate())
                .createdAt(appointment.getCreatedDate())
                .updatedAt(appointment.getUpdatedDate())
                .build();
    }
}
