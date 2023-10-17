package ua.com.foxminded.config;

import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ConfigurationPropertiesScan("ua.com.foxminded.config")
public class TestConfig {
    
    @Bean
    @ConfigurationProperties("keycloak.policy-enforcer-config")
    PolicyEnforcerConfig policyEnforcerConfigy() {
        return new PolicyEnforcerConfig();
    }
    
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
