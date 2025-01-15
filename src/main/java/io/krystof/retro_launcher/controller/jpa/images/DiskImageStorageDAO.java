package io.krystof.retro_launcher.controller.jpa.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Component
public class DiskImageStorageDAO {

    private S3Client s3Client;

    private String bucketName;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskImageStorageDAO.class);

    public DiskImageStorageDAO(S3Client s3Client, @Value("${aws.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }


    public Resource getDiskImageAsResource(String storagePath) {
        LOGGER.info("Fetching disk image from S3: {} and bucket {}", storagePath, bucketName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storagePath)
                .build();
        return new InputStreamResource(s3Client.getObject(getObjectRequest));

    }
}
