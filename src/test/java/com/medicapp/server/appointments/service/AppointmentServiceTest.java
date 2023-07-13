package com.medicapp.server.appointments.service;


import com.medicapp.server.appoinments.dto.AppointmentRequest;
import com.medicapp.server.appoinments.dto.AppointmentResponse;
import com.medicapp.server.appoinments.model.Appointment;
import com.medicapp.server.appoinments.repository.AppointmentRepository;
import com.medicapp.server.appoinments.service.AppointmentService;
import com.medicapp.server.appoinments.utils.AppointmentHelper;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.TimeSheet;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.doctors.repository.TimeSheetRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private TimeSheetRepository timeSheetRepository;

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentHelper appointmentHelper;

    @Test
    public void testGetAppointments() {
        // Arrange
        int page = 0;
        int size = 10;
        LocalDateTime date = LocalDateTime.now();
        boolean month = false;
        String specialty = "Cardiology";
        int userId = 123;

        Pageable pageable = PageRequest.of(page, size);
        List<Appointment> appointments = Arrays.asList(
                new Appointment(/* appointment details */),
                new Appointment(/* appointment details */)
        );
        Page<Appointment> appointmentPage = new PageImpl<>(appointments, pageable, appointments.size());
        List<AppointmentResponse> appointmentResponses = Arrays.asList(
                new AppointmentResponse(/* response details */),
                new AppointmentResponse(/* response details */)
        );

        when(appointmentRepository.findByDateContaining(date, userId, pageable)).thenReturn(appointmentPage);
        when(appointmentHelper.mapToAppoinmentResponse(any(Appointment.class))).thenReturn(
                new AppointmentResponse(/* response details */));

        // Act
        Page<AppointmentResponse> result = appointmentService.getAppointments(
                page, size, date, month, specialty, userId);

        // Assert
        assertEquals(appointmentResponses.size(), result.getContent().size());
        // Add more assertions for other fields, if needed
        verify(appointmentRepository, times(1)).findByDateContaining(date, userId, pageable);
        verify(appointmentHelper, times(appointments.size())).mapToAppoinmentResponse(any(Appointment.class));
    }
    @Test
    public void AddAppointmentTest(){
        User user = User.builder()
                .id(10)
                .firstname("UserTest")
                .lastname("userTest")
                .email("usertest@example.com")
                .build();
        User user2 = User.builder()
                .id(11)
                .firstname("doctorTest")
                .lastname("Test")
                .email("doctortest@example.com")
                .build();
        Doctor doctor = Doctor.builder()
                .id(10)
                .user(user2)
                .description("description")
                .details("details")
                .rating(0F)
                .cost(12F)
                .build();

        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
                .doctor_id(10)
                .user_id(10)
                .date(LocalDateTime.now())
                .build();
        TimeSheet timeSheet = TimeSheet.builder()
                .id(1)
                .doctor(doctor)
                .date(appointmentRequest.getDate())
                .build();

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(doctorRepository.findById(10)).thenReturn(Optional.of(doctor));
        when(timeSheetRepository.findTimeSheetByDate(appointmentRequest.getDate(),doctor)).thenReturn(Optional.of(timeSheet));
        when(appointmentRepository.save(any())).thenReturn(null);
        appointmentService.addAppointment(appointmentRequest);
    }

    @Test
    public void deleteAppointmentTest (){
        //Elaborate a test for the deleteAppointment method
        Appointment appointment1 = Appointment.builder()
                .id(1)
                .doctor(Doctor.builder().id(1).build())
                .user(User.builder().id(1).build())
                .date(LocalDateTime.now())
                .build();
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment1));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(appointment1.getDoctor()));
        when(timeSheetRepository.findTimeSheetByDate(appointment1.getDate(),appointment1.getDoctor())).thenReturn(Optional.of(TimeSheet.builder().id(1).build()));
        when(timeSheetRepository.save(any())).thenReturn(null);
    }


}
