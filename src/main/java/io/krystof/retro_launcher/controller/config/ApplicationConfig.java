package io.krystof.retro_launcher.controller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
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