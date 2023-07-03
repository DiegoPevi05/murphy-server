package com.medicapp.server.appoinments.service;

import com.medicapp.server.appoinments.dto.AppointmentRequest;
import com.medicapp.server.appoinments.dto.AppointmentResponse;
import com.medicapp.server.appoinments.model.Appointment;
import com.medicapp.server.appoinments.repository.AppointmentRepository;
import com.medicapp.server.appoinments.utils.AppointmentHelper;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.TimeSheet;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.doctors.repository.TimeSheetRepository;
import com.medicapp.server.doctors.utils.DoctorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final DoctorHelper doctorHelper;
    private final AppointmentHelper appointmentHelper;

    private final AuthenticationService authenticationService;

    public Page<AppointmentResponse>getAppointmentsUser(int page,
                                                        int size,
                                                        LocalDateTime date,
                                                        boolean month,
                                                        String specialty){
        User user = authenticationService.getUserAuthorize();
        return getAppointments(page,size,date,month,specialty,user.getId());
    }
    public Page<AppointmentResponse> getAppointments(int page,
                                                    int size,
                                                    LocalDateTime date,
                                                    boolean month,
                                                    String specialty,
                                                     int user_id

    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = null;
        if(date != null && !month) {
            appointmentPage = appointmentRepository.findByDateContaining(date,user_id, pageable);
        } else if (date != null && month) {
            appointmentPage = appointmentRepository.findByDateContainingMonth(date,user_id, pageable);
        }else if(specialty != null && !specialty.isEmpty()){
            appointmentPage = appointmentRepository.findBySpecialtyContaining(doctorHelper.getSpecialty(specialty),user_id,pageable);
        }else{
            appointmentPage = appointmentRepository.findAllByUserId(user_id,pageable);
        }

        List<AppointmentResponse> appointmentResponses = appointmentPage.getContent().stream()
                .map(appointmentHelper::mapToAppoinmentResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(appointmentResponses, pageable, appointmentPage.getTotalElements());
    }

    public void addAppointmentUser(AppointmentRequest appointmentRequest){
        User user = authenticationService.getUserAuthorize();
        if(!Objects.equals(user.getId(), appointmentRequest.getUser_id())){
            throw new IllegalArgumentException("You do not have permission to perform this action");
        }
        addAppointment(appointmentRequest);
    }

    public void addAppointment(AppointmentRequest appointmentRequest){
        User user = userRepository.findById(appointmentRequest.getUser_id())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "User with id "+appointmentRequest.getUser_id()+ " does not exist"
                ));
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctor_id())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor with id "+appointmentRequest.getDoctor_id()+ " does not exist"
                ));
        TimeSheet timeSheet = timeSheetRepository.findTimeSheetByDate(appointmentRequest.getDate(), doctor)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                "No appointments available for the date "+appointmentRequest.getDate()+ " and the doctor "+ doctor.getUser().getFirstname()
        ));

        timeSheet.setIsAvailable(false);
        timeSheetRepository.save(timeSheet);

        Appointment appointment = Appointment.builder()
                        .user(user)
                        .doctor(doctor)
                        .date(appointmentRequest.getDate())
                        .build();

        appointmentRepository.save(appointment);
    };

    public void deleteAppointmentUser(int Appointment_id){
        User user = authenticationService.getUserAuthorize();
        Appointment appointment = appointmentRepository.findById(Appointment_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Appointment with id "+Appointment_id+ " does not exist"
                ));
        if(!Objects.equals(appointment.getUser(), user)){
            throw new IllegalArgumentException("You do not have permission to do this action");
        }
        deleteAppointment(Appointment_id);
    }

    public void deleteAppointmentDoctor(int Appointment_id){
        User user = authenticationService.getUserAuthorize();
        Appointment appointment = appointmentRepository.findById(Appointment_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Appointment with id "+Appointment_id+ " does not exist"
                ));
        if(!Objects.equals(appointment.getDoctor().getUser(), user)){
            throw new IllegalArgumentException("You do not have permission to do this action");
        }
        deleteAppointment(Appointment_id);
    }

    public void deleteAppointment(int Appointment_id){
        Appointment appointment = appointmentRepository.findById(Appointment_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Appointment with id "+Appointment_id+ " does not exist"
                ));
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor with id "+appointment.getDoctor().getId()+ " does not exist"
                ));

        TimeSheet timeSheet = timeSheetRepository.findTimeSheetByDate(appointment.getDate(), doctor)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "There are not appointments for the date "+appointment.getDate()+ "for the doctor"+ doctor.getUser().getFirstname()
                ));

        timeSheet.setIsAvailable(true);
        timeSheetRepository.save(timeSheet);
        appointmentRepository.deleteById(Appointment_id);

    }

}
