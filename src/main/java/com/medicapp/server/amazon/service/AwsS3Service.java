package com.medicapp.server.amazon.service;

import com.medicapp.server.amazon.repository.AwsS3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AwsS3Repository awsS3Repository;

    public String uploadFile(String bucketName, MultipartFile file, String fileKey, String oldFileKey) {
        File fileObj = convertMultiPartFileToFile(file);
        deleteFile(bucketName,oldFileKey);
        awsS3Repository.uploadFile(bucketName, fileKey, fileObj);
        return awsS3Repository.getObjectUrl(bucketName,fileKey);
    }

    public void deleteFile(String bucketName,String key){
        if(key != null){
            if(awsS3Repository.isObjectExits(bucketName,key)){
                awsS3Repository.deleteObject(bucketName,key);
            }
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.format("Error converting multipartFile to file :\n %s", e);
        }
        return convertedFile;
    }


}