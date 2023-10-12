package ua.com.foxminded.cars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan("ua.com.foxminded.cars.config")
public class TestConfig {
    
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
