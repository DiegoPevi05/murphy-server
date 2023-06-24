package com.medicapp.server.doctors.utils;

import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.Specialty;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.config.ExceptionHandlerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorHelper {

    public DoctorResponse mapToDoctorResponse(Doctor doctor){
        return DoctorResponse.builder()
                .doctor_id(doctor.getId())
                .firstname(doctor.getUser().getFirstname())
                .lastname(doctor.getUser().getLastname())
                .email(doctor.getUser().getEmail())
                .profile_url(doctor.getUser().getProfile_url())
                .specialty(doctor.getSpecialty())
                .description(doctor.getDescription())
                .details(doctor.getDetails())
                .rating(doctor.getRating())
                .cost(doctor.getCost())
                .build();
    }

    public Doctor mapToDoctor(DoctorRequest doctorRequest, User user){

        if(doctorRequest.getSpecialty() ==null ||
                doctorRequest.getSpecialty().isEmpty()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Ingresa la Especialidad del medico");
        }

        if(doctorRequest.getDescription() ==null ||
                doctorRequest.getDescription().isEmpty()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Ingresa la Descripcion del medico");
        }
        if(doctorRequest.getDetails() ==null ||
                doctorRequest.getDetails().isEmpty()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Ingresa los Detalles del medico");
        }

        if(doctorRequest.getCost() == null ||
                doctorRequest.getCost() == 0){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Ingresa el Costo del medico");
        }

        return Doctor.builder()
                .user(user)
                .specialty(getSpecialty(doctorRequest.getSpecialty()))
                .description(doctorRequest.getDescription())
                .details(doctorRequest.getDetails())
                .rating(0F)
                .cost(doctorRequest.getCost())
                .build();
    }

    public Specialty getSpecialty(String specialty) {
        try {
            return Specialty.valueOf(specialty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Specialty "+specialty+" no existe");
        }
    }


}
