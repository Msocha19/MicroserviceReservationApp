package pl.lodz.p.it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AppRest extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AppRest.class, args);
    }
}
