package ua.com.foxminded.cars.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class KeycloakConfig {
    
    @Value("${keycloak.policy-enforcer-config.realm}")
    String realm; 
    
    @Value("${keycloak.policy-enforcer-config.resource}")
    String resource;
    
    @Value("${keycloak.policy-enforcer-config.credentials.secret}")
    String secret;
}
