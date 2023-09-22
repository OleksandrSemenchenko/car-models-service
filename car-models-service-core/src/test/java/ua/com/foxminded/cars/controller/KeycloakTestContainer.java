package ua.com.foxminded.cars.controller;


import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Container;

import dasniko.testcontainers.keycloak.KeycloakContainer;

abstract class KeycloakTestContainer {
    
    public static final String USER_NAME_ADMIN = "admin";
    public static final String CLIENT_SECRET = "secret";
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    public static final String JWK_SET_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri";
    public static final String AUTH_SERVER_URL_PROPERTY = "keycloak.policy-enforcer-config.auth-server-url";
    
    
    @Autowired
    private PolicyEnforcerConfig policyEnforcerConfig;
    
    @Container
    static KeycloakContainer keycloak;
    
    static {
        keycloak = new KeycloakContainer().withRealmImportFile(REALM_CONFIG_FILE_PATH);
        keycloak.start();
    }
    
    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(JWK_SET_URI_PROPERTY, 
                     () -> keycloak.getAuthServerUrl() + "/realms/car-services/protocol/openid-connect/certs");
        registry.add(AUTH_SERVER_URL_PROPERTY, keycloak::getAuthServerUrl);
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
                                                         policyEnforcerConfig.getRealm(),
                                                         username,
                                                         password,
                                                         policyEnforcerConfig.getResource(),
                                                         (String) policyEnforcerConfig.getCredentials()
                                                                                      .get(CLIENT_SECRET));
        return keycloakInstance.tokenManager().getAccessTokenString();
    }
}
