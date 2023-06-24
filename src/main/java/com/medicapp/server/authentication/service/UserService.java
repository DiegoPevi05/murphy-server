package com.medicapp.server.authentication.service;

import com.medicapp.server.amazon.service.AwsS3Service;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.dto.UserResponse;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.config.ExceptionHandlerConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    private final AwsS3Service awsS3Service;

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
}
