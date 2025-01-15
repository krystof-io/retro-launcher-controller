package io.krystof.retro_launcher.controller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;


@Configuration
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public S3Client s3Client(@Value("${aws.accessKey}") String accessKey,
                             @Value("${aws.secretKey}") String secretKey,
                             @Value("${aws.endpointUrl}") String endpointUrl) {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .forcePathStyle(true) //Required for minio
                .build();
        return s3;
    }
//    @Bean
//    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
//        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
//        LogbackValve logbackValve = new LogbackValve();
//        logbackValve.setFilename("logback-access.xml");
//        tomcatServletWebServerFactory.addContextValves(logbackValve);
//        return tomcatServletWebServerFactory;
//    }
}