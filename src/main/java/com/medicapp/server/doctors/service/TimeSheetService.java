package com.medicapp.server.doctors.service;

import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.dto.TimeSheetRequest;
import com.medicapp.server.doctors.dto.TimeSheetResponse;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.TimeSheet;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.doctors.repository.TimeSheetRepository;
import com.medicapp.server.doctors.utils.TimeSheetHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TimeSheetService {

    private final DoctorRepository doctorRepository;
    private final TimeSheetRepository timeSheetRepository;

    private final AuthenticationService authenticationService;
    private final TimeSheetHelper timeSheetHelper;

    public  List<TimeSheetResponse> getTimeSheetsDoctor(LocalDateTime date){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        List<TimeSheet> times = null;
        if(date != null){
            times = timeSheetRepository.findByDateContaining(date,doctor.getId());
        }else{
            times = timeSheetRepository.findByDoctor(doctor);
        }

        List<TimeSheetResponse> timeSheetResponseList = times.stream()
                .map(timeSheetHelper::mapToTimeSheetResponse)
                .toList();
        return timeSheetResponseList;
    };

    public List<TimeSheetResponse> getTimeSheets(LocalDateTime date, Boolean week, Integer Doctor_id){

        boolean IsDoctorExist = doctorRepository.existsById(Doctor_id);
        if(!IsDoctorExist){
            throw new ExceptionHandlerConfig.ResourceNotFoundException(
                    "Doctor con id "+Doctor_id+ " no existe"
            );
        }

        List<TimeSheet> times = null;
        if(date != null){
            times = timeSheetRepository.findByDateContaining(date,Doctor_id);
        }else if(date != null && !week){
            times  = timeSheetRepository.findByDateContainingWeek(date,Doctor_id);
        }else{
            LocalDateTime now = LocalDateTime.now();
            times  = timeSheetRepository.findByDateContainingWeek(now,Doctor_id);
        }
        List<TimeSheetResponse> timeSheetResponseList = times.stream()
                .map(timeSheetHelper::mapToTimeSheetResponse)
                .toList();

        return timeSheetResponseList;
    }

    public void addTimeSheetsByDoctor(TimeSheetRequest timeSheetRequest){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(!Objects.equals(doctor.getId(), timeSheetRequest.getDoctor_id())){
            throw new IllegalArgumentException("No tiene permisos para realizar esta accion");
        }
        addTimeSheets(timeSheetRequest);
    };

    public void addTimeSheets(TimeSheetRequest timeSheetRequest){
        Doctor doctor = doctorRepository.findById(timeSheetRequest.getDoctor_id()).orElseThrow(
                () -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor con id "+timeSheetRequest.getDoctor_id()+ " no existe"
                )
        );

        if(!timeSheetRequest.getDate().isAfter(LocalDateTime.now().plusDays(1))){
            throw new ExceptionHandlerConfig.ResourceNotFoundException(
                    "Date can not be less than tomorrow"
            );
        }
        TimeSheet timeSheet = TimeSheet.builder()
                .doctor(doctor)
                .date(timeSheetRequest.getDate())
                .isAvailable(true)
                .build();
        timeSheetRepository.save(timeSheet);
    };

    public void deleteTimeSheetsByDoctor(Integer TimeSheetId, Integer doctorId){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(!Objects.equals(doctor.getId(), doctorId)){
            throw new IllegalArgumentException("No tiene permisos para realizar esta accion");
        }
        deleteTimeSheet(TimeSheetId);
    }

    public void deleteTimeSheet(Integer TimeSheetId){
        TimeSheet timeSheet = timeSheetRepository.findById(TimeSheetId).orElseThrow(
                () -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "TimeSheet with id "+TimeSheetId+ " doest not exist"
                )
        );

        timeSheetRepository.delete(timeSheet);
    }
}
