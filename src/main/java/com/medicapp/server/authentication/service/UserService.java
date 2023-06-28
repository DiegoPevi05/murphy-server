package com.medicapp.server.authentication.service;

import com.medicapp.server.amazon.service.AwsS3Service;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.dto.UserResponse;
import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.dto.DoctorRequest;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    private final AwsS3Service awsS3Service;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;

    @Value("${default.image.profile_user_male}")
    private String male_profile_image;

    @Value("${default.image.profile_user_female}")
    private String female_profile_image;

    private String bucketName =  "medicapp-bucket";
    private String filePath =  "ProfilesImages/";

    public UserResponse getMe(){
        User user =  authenticationService.getUserAuthorize();
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .sex(user.getSex())
                .profile_url(user.getProfile_url())
                .role(user.getRole())
                .build();
    }

    public void addUser(RegisterRequest registerRequest){
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException(
                    "User with email "+registerRequest.getEmail()+ " already exists");
        }

        String defaultProfileImage = "";
        if(!registerRequest.getSex().isEmpty() && registerRequest.getSex().equals("male")){
            defaultProfileImage = male_profile_image;
        }else if(!registerRequest.getSex().isEmpty() && registerRequest.getSex().equals("female")){
            defaultProfileImage = female_profile_image;
        }else{
            defaultProfileImage = male_profile_image;
        }

        var user = User.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .sex(registerRequest.getSex())
                .enabled(true)
                .profile_url(defaultProfileImage)
                .build();
        var savedUser = userRepository.save(user);
    };

    @Transactional
    public void updateUserByUser(Integer User_id, UserRequest userRequest){
        User user = authenticationService.getUserAuthorize();
        if(!Objects.equals(user.getId(), User_id)){
            throw new IllegalArgumentException("No tiene permisos para realizar esta accion");
        }
        updateUser(User_id,userRequest);
    }

    @Transactional
    public void updateUser(Integer User_id, UserRequest userRequest){
        User user = userRepository.findById(User_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctor con id " + User_id + " no existe"
                ));
        if (userRequest.getFirstname() != null && !userRequest.getFirstname().isEmpty() && !Objects.equals(userRequest.getFirstname(), user.getFirstname())) {
            user.setFirstname(userRequest.getFirstname());
        }
        if(userRequest.getLastname() != null && !userRequest.getLastname().isEmpty() && !Objects.equals(userRequest.getLastname(), user.getLastname())){
            user.setLastname(userRequest.getLastname());
        }
        if(userRequest.getEmail() != null && !userRequest.getEmail().isEmpty() && !Objects.equals(userRequest.getEmail(), user.getEmail())){
            user.setEmail(userRequest.getEmail());
        }
    }

    @Transactional
    public void updateUserProfileImage(MultipartFile file_1){
        User user = authenticationService.getUserAuthorize();
        if(file_1 != null && !file_1.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file_1.getOriginalFilename();
            String fileKey =  filePath+fileName;
            String UrlS3Object =  awsS3Service.uploadFile(bucketName,file_1,fileKey,user.getProfile_key());
            user.setProfile_key(fileKey);
            user.setProfile_url(UrlS3Object);
        }
    }

    public void deleteUser(int User_id){
        User user = userRepository.findById(User_id)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "User con id " + User_id + " no existe"
                ));

        Optional<Doctor> doctor = doctorRepository.findByUserEmail(user.getEmail());
        //if User is Doctor delete Doctor
        doctor.ifPresent(value -> doctorRepository.deleteById(value.getId()));
        userRepository.deleteById(user.getId());
    }
}
