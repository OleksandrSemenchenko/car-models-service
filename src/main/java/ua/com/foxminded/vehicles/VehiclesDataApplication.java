package ua.com.foxminded.vehicles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VehiclesDataApplication {

    public static final String DEV_PROFILE = "dev";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VehiclesDataApplication.class);
        application.setAdditionalProfiles(DEV_PROFILE);
        application.run(args);
    }
}
