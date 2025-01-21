package io.krystof.retro_launcher.controller.jpa.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Component
public class DiskImageStorageDAO {
    private static final Logger logger = LoggerFactory.getLogger(DiskImageStorageDAO.class);

    private final S3Client s3Client;
    private final String bucketName;

    public DiskImageStorageDAO(S3Client s3Client, @Value("${aws.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void storeDiskImage(String storagePath, MultipartFile file) throws IOException {
        logger.info("Storing disk image at path: {} in bucket: {}", storagePath, bucketName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("Successfully stored disk image at: {}", storagePath);
        } catch (Exception e) {
            logger.error("Failed to store disk image: {}", e.getMessage());
            throw new IOException("Failed to store disk image", e);
        }
    }

    public Resource getDiskImageAsResource(String storagePath) throws IOException {
        logger.info("Fetching disk image from path: {} in bucket: {}", storagePath, bucketName);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            return new InputStreamResource(s3Client.getObject(getObjectRequest));
        } catch (Exception e) {
            logger.error("Failed to fetch disk image: {}", e.getMessage());
            throw new IOException("Failed to fetch disk image", e);
        }
    }

    public void deleteDiskImage(String storagePath) throws IOException {
        logger.info("Deleting disk image at path: {} from bucket: {}", storagePath, bucketName);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Successfully deleted disk image at: {}", storagePath);
        } catch (Exception e) {
            logger.error("Failed to delete disk image: {}", e.getMessage());
            throw new IOException("Failed to delete disk image", e);
        }
    }

    public boolean checkExists(String storagePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}