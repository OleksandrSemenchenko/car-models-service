package ua.com.foxminded.cars.controller;


import org.keycloak.admin.client.Keycloak;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Container;

import dasniko.testcontainers.keycloak.KeycloakContainer;

abstract class KeycloakTestContainer {
    
    public static final String CAR_SERVICES_REALM = "car-services";
    public static final String CAR_MODELS_SERVICE_CLIENT = "car-models-service";
    public static final String USER_NAME_ADMIN = "admin";
    public static final String CLIENT_SECRET = "eGtdvx8kd5XZ2ThQ1YlqWS5y7kuTGJwk";
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    
    @Container
    static KeycloakContainer keycloak;
    
    static {
        keycloak = new KeycloakContainer().withRealmImportFile(REALM_CONFIG_FILE_PATH);
        keycloak.start();
    }
    
    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", 
                     () -> keycloak.getAuthServerUrl() + "/realms/car-services/protocol/openid-connect/certs");
        registry.add("keycloak.policy-enforcer-config.auth-server-url", keycloak::getAuthServerUrl);
    }
    
    
    public RequestPostProcessor bearerTokenFor(String username) {
        String token = getToken(username, username);

        return new RequestPostProcessor() {
            
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.addHeader("Authorization", "Bearer " + token);
                return request;
            }
        };
    }
    
    private String getToken(String username, String password) {
        Keycloak keycloakInstance = Keycloak.getInstance(keycloak.getAuthServerUrl(),
                                                         CAR_SERVICES_REALM,
                                                         username,
                                                         password,
                                                         CAR_MODELS_SERVICE_CLIENT,
                                                         CLIENT_SECRET);
        return keycloakInstance.tokenManager().getAccessTokenString();
    }
}
