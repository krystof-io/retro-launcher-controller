package io.krystof.retro_launcher.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
class RetroLauncherControllerApplicationTests {

	@MockitoBean
	S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
