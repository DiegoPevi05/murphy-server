package com.medicapp.server.amazon.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.net.URL;

@Repository
public class AwsS3Repository {

    private AmazonS3 s3Client;

    @Autowired
    public AwsS3Repository(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public boolean isObjectExits(String bucketName, String fileKey) {
        return s3Client.doesObjectExist(bucketName,fileKey);
    }

    public void deleteObject (String bucketName, String fileKey) {
        s3Client.deleteObject(bucketName, fileKey);
    }
    public String getObjectUrl(String bucketName, String fileKey){
        URL objectUrl = s3Client.getUrl(bucketName, fileKey);
        return objectUrl.toString();
    }

    public void uploadFile(String bucketName, String fileName, File fileObj) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, fileObj);
        s3Client.putObject(putObjectRequest);
        fileObj.delete();
    }
}
