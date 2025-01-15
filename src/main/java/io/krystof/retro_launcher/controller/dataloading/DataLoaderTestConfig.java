package io.krystof.retro_launcher.controller.dataloading;


import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@EnableJpaRepositories(basePackages = "io.krystof.retro_launcher.controller.jpa.repositories")
@EnableTransactionManagement
@PropertySource("classpath:application-local.properties")
@ComponentScan(basePackages = "io.krystof.retro_launcher.controller.dataloading")
@Profile("oneshot")
public class DataLoaderTestConfig {
    @Bean
    public DataSource dataSource(@Value("${spring.datasource.url}") String dataSourceUrl,
                                 @Value("${spring.datasource.username}") String dataSourceUsername,
                                 @Value("${spring.datasource.password}") String dataSourcePassword) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceUrl);
        dataSource.setUsername(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("io.krystof.retro_launcher.controller.jpa.entities");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
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

}
