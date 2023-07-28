package ua.com.foxminded.vehicles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:config.properties")
@ConfigurationPropertiesScan("ua.com.foxminded.vehicles.config")
public class VehiclesServiceApplication {

    public static final String DEV_PROFILE = "dev";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VehiclesServiceApplication.class);
        application.setAdditionalProfiles(DEV_PROFILE);
        application.run(args);
    }
}
