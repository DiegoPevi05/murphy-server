package com.medicapp.server.doctors.service;

import com.medicapp.server.amazon.service.AwsS3Service;
import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.doctors.utils.DoctorHelper;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.config.ExceptionHandlerConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorHelper doctorHelper;

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    private final AwsS3Service awsS3Service;

    private String bucketName =  "medicapp-bucket";
    private String filePath =  "ProfilesImages/";

    @Value("${default.image.profile_doctor_male}")
    private String male_profile_image_doctor;

    @Value("${default.image.profile_doctor_female}")
    private String female_profile_image_doctor;

    public DoctorResponse getMe(){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        return doctorHelper.mapToDoctorResponse(doctor);
    }
    public Page<DoctorResponse> getDoctors(int page, int size, String name, String specialty){
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> productPage = null;
        if(name != null && !name.isEmpty()){
            productPage = doctorRepository.findByNameContaining(name,pageable);
        }else if(specialty != null && !specialty.isEmpty()){
            productPage = doctorRepository.findBySpecialtyContaining(doctorHelper.getSpecialty(specialty),pageable);
        }else{
            productPage = doctorRepository.findAll(pageable);
        }

        List<DoctorResponse> productResponses = productPage.getContent().stream()
                .map(doctorHelper::mapToDoctorResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    public void addDoctor(DoctorRequest doctorRequest){
        User user = userRepository.findById(doctorRequest.getUser_id())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Usuario con id "+doctorRequest.getUser_id()+ " no existe"
                ));

        if(!user.getSex().isEmpty() && user.getSex().equals("male")){
            user.setProfile_url(male_profile_image_doctor);
        }else if(!user.getSex().isEmpty() && user.getSex().equals("female")){
            user.setProfile_url(female_profile_image_doctor);
        }else{
            user.setProfile_url(male_profile_image_doctor);
        }
        user.setRole(Role.ROLE_DOCTOR);
        userRepository.save(user);

        Doctor doctor = doctorHelper.mapToDoctor(doctorRequest,user);
        doctorRepository.save(doctor);
    };
    @Transactional
    public void updateDoctorByDoctor(Integer Doctor_id,DoctorRequest doctorRequest){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(!Objects.equals(doctor.getId(), Doctor_id)){
            throw new IllegalArgumentException("No tiene permisos para realizar esta accion");
        }
        updateDoctor(Doctor_id,doctorRequest);
    }

    @Transactional
    public void updateDoctor(Integer Doctor_id,DoctorRequest doctorRequest) {
        Doctor doctor = doctorRepository.findById(Doctor_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor con id " + Doctor_id + " no existe"
                ));
        if (doctorRequest.getSpecialty() != null && !doctorRequest.getSpecialty().isEmpty() && !Objects.equals(doctorHelper.getSpecialty(doctorRequest.getSpecialty()), doctor.getSpecialty())) {
            doctor.setSpecialty(doctorHelper.getSpecialty(doctorRequest.getSpecialty()));
        }
        if (doctorRequest.getDescription() != null && !doctorRequest.getDescription().isEmpty() && !Objects.equals(doctorRequest.getDescription(), doctor.getDescription())) {
            doctor.setDescription(doctorRequest.getDescription());
        }
        if (doctorRequest.getCost() != null && doctorRequest.getCost() > 0 && !Objects.equals(doctorRequest.getCost(), doctor.getCost())) {
            doctor.setCost(doctorRequest.getCost());
        }
        if (doctorRequest.getDetails() != null && !doctorRequest.getDetails().isEmpty() && !Objects.equals(doctorRequest.getDetails(), doctor.getDetails())) {
            doctor.setDetails(doctorRequest.getDetails());
        }
    }

    @Transactional
    public void updateDoctorProfileImage(MultipartFile file_1){
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(file_1 != null && !file_1.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file_1.getOriginalFilename();
            String fileKey =  filePath+fileName;
            String UrlS3Object =  awsS3Service.uploadFile(bucketName,file_1,fileKey,doctor.getUser().getProfile_key());
            doctor.getUser().setProfile_key(fileKey);
            doctor.getUser().setProfile_url(UrlS3Object);
        }

    }

    public void deleteDoctor(int Doctor_id){
        Doctor doctor = doctorRepository.findById(Doctor_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor con id " + Doctor_id + " no existe"
                ));
        User user = doctor.getUser();
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        doctorRepository.deleteById(Doctor_id);
    }
}
