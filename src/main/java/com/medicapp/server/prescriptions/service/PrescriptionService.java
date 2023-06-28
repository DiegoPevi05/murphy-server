package com.medicapp.server.prescriptions.service;

import com.medicapp.server.amazon.service.AwsS3Service;
import com.medicapp.server.appoinments.model.Appointment;
import com.medicapp.server.appoinments.repository.AppointmentRepository;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.prescriptions.model.Prescription;
import com.medicapp.server.prescriptions.repository.PrescriptionRepository;
import com.medicapp.server.prescriptions.utils.PrescriptionHelper;
import com.medicapp.server.prescriptions.dto.PrescriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final AwsS3Service awsS3Service;
    private final PrescriptionHelper prescriptionHelper;
    private final AuthenticationService authenticationService;
    private String bucketName =  "medicapp-bucket";
    private String filePath =  "Prescriptions/";

    public Page<PrescriptionResponse>getPrescriptionByDoctor(int page, int size, Integer appointment_id){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(appointment_id != null && appointment_id > 0){
            Appointment appointment = appointmentRepository.findById(appointment_id)
                    .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));
            if(!Objects.equals(appointment.getDoctor().getId(), doctor.getId())){
                throw new ExceptionHandlerConfig.ResourceNotFoundException("Not authorized");
            }
        }
        return getPrescriptions(page,size,0,appointment_id);
    }

    public Page<PrescriptionResponse>getPrescriptionsByUser(int page, int size, Integer appointment_id){
        User user = authenticationService.getUserAuthorize();
        if(appointment_id != null && appointment_id > 0){
            Appointment appointment = appointmentRepository.findById(appointment_id)
                    .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));
            if(!Objects.equals(appointment.getUser().getId(), user.getId())){
                throw new ExceptionHandlerConfig.ResourceNotFoundException("Not authorized");
            }
        }
        return getPrescriptions(page,size,0,appointment_id);
    }


    public Page<PrescriptionResponse> getPrescriptions(int page, int size, Integer user_id, Integer appointment_id){
        Pageable pageable = PageRequest.of(page, size);
        Page<Prescription> prescriptionPage = null;
        if(user_id > 0 && appointment_id == 0){
            prescriptionPage = prescriptionRepository.findByUserContaining(user_id,pageable);
        }else if(appointment_id > 0 && user_id == 0) {
            prescriptionPage = prescriptionRepository.findByAppointmentContaining(appointment_id, pageable);
        }else{
            prescriptionPage = prescriptionRepository.findAll(pageable);
        }

        List<PrescriptionResponse> prescriptionResponses = prescriptionPage.getContent().stream()
                .map(prescriptionHelper::mapToPrescriptionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(prescriptionResponses, pageable, prescriptionPage.getTotalElements());
    }



    public void addPrescriptionByDoctor(Integer appointment_id ,
                                      String prescription_content,
                                      MultipartFile file_1){
        Appointment appointment = appointmentRepository.findById(appointment_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));

        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(!Objects.equals(appointment.getDoctor().getId(), doctor.getId())){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Not authorized");
        }
        addPrescription(appointment_id, prescription_content,file_1);
    }

    public void addPrescription(Integer appointment_id ,
                                String prescription_content,
                                MultipartFile file_1){
        Appointment appointment = appointmentRepository.findById(appointment_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));

        Prescription prescription = Prescription.builder().build();
        prescription.setAppointment(appointment);
        if(prescription_content != null && !prescription_content.isEmpty()){
            prescription.setPrescription_content(prescription_content);
        }

        if(file_1 != null && !file_1.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file_1.getOriginalFilename();
            String fileKey =  filePath+fileName;
            String UrlS3Object =  awsS3Service.uploadFile(bucketName,file_1,fileKey,prescription.getPrescription_image_key());
            prescription.setPrescription_image_key(fileKey);
            prescription.setPrescription_image_url(UrlS3Object);
        }
        prescriptionRepository.save(prescription);
    }

    public void deletePrescription(Integer prescription_id){
        Prescription prescription = prescriptionRepository.findById(prescription_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Prescription not found"));
        prescriptionRepository.delete(prescription);
    }

}
