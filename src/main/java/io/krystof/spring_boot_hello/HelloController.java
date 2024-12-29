package io.krystof.spring_boot_hello;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HelloController {

    private final Environment environment;
    private final String workdir;
    BuildProperties buildProperties;

    @Autowired
    public HelloController(Environment environment, @Value("${workdir}") String workdir, BuildProperties buildProperties) {
        this.environment = environment;
        this.workdir = workdir;
        this.buildProperties = buildProperties;
    }

    @GetMapping("/")
    public String index(@RequestParam(name = "delayInMs", required = false, defaultValue = "0") int delayInMs, HttpServletRequest request)  {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body><pre>");
        sb.append("Greetings ").append(request.getRemoteAddr())
                .append(" from ").append(buildProperties.getVersion()).append("!\n");
        sb.append("</pre></body></html>");

        return sb.toString();
    }

}
