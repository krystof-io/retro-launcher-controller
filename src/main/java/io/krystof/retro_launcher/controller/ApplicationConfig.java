package io.krystof.retro_launcher.controller;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Configuration
public class ApplicationConfig {

//    @Bean
//    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
//        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
//        LogbackValve logbackValve = new LogbackValve();
//        logbackValve.setFilename("logback-access.xml");
//        tomcatServletWebServerFactory.addContextValves(logbackValve);
//        return tomcatServletWebServerFactory;
//    }
}