package ua.com.foxminded.cars.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;


import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.cars.TestConfig;
import ua.com.foxminded.cars.config.KeycloakConfig;

@Slf4j
//@SpringBootTest(classes = TestConfig.class)
public class TestContainers {
    
    public static final String ADMIN_USER = "admin";
    public static final String CLIENT_SECRET = "secret";
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    public static final String JWK_SET_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri";
    public static final String AUTH_SERVER_URL_PROPERTY = "keycloak.policy-enforcer-config.auth-server-url";
    
    
    static Network network = Network.newNetwork();
//    @Autowired
//    private KeycloakConfig keycloakConfig;
    
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
//            .withNetwork(network)
            .withDatabaseName("cars")
            .withUsername("cars")
            .withPassword("1234")
            .withExposedPorts(5432)
            .withLogConsumer(new Slf4jLogConsumer(log));
    
    
    static GenericContainer<?> carModelsService;
    
    static {
        postgres.start();
        carModelsService = new GenericContainer<>("cars/car-models-service-core:latest")
//                .withNetwork(network)
//                .withEnv("POSTGRES_HOST", postgres.getHost() + ":" + postgres.getMappedPort(5432))
                .withExposedPorts(8181)
                .withLogConsumer(new Slf4jLogConsumer(log));
        carModelsService.start();
    }
    
//    @Container
//    static KeycloakContainer keycloak;
//    
//    static {
//        keycloak = new KeycloakContainer().withRealmImportFile(REALM_CONFIG_FILE_PATH)
//                                          .withLogConsumer(new Slf4jLogConsumer(log));
//        keycloak.start();
//    }
    
    public static String carModelSeviceAsdress = carModelsService.getHost() + ":" + 
                                                 carModelsService.getMappedPort(8181);
    
//    public RequestPostProcessor bearerTokenFor(String username) {
//        String token = getToken(username, username);
//
//        return new RequestPostProcessor() {
//            
//            @Override
//            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
//                request.addHeader("Authorization", "Bearer " + token);
//                return request;
//            }
//        };
//    }
    
    /*
    public String getAdminRoleBearerToken() {
        return getBearerToken(ADMIN_USER, ADMIN_USER);
    }
    
    public String getBearerToken(String username, String password) {
        Keycloak keycloakInstance = Keycloak.getInstance(keycloak.getAuthServerUrl(),
                                                         keycloakConfig.getRealm(),
                                                         username,
                                                         password,
                                                         keycloakConfig.getResource(),
                                                         keycloakConfig.getSecret());
        String accessToken = "";
        
        try {
            accessToken = keycloakInstance.tokenManager().getAccessTokenString();
            
        } catch (Exception e) {
            log.error("Access token error", e);
        }
        
        
        
        
        return "Bearer " + accessToken;
    }
    */
    
//    private String getBearerToken(String username, String password) {
//        try (Keycloak keycloakInstance = KeycloakBuilder.builder().serverUrl(keycloak.getAuthServerUrl())
//                                                                  .realm(keycloakConfig.getRealm())
//                                                                  .clientId(keycloakConfig.getResource())
//                                                                  .username(username)
//                                                                  .password(password)
//                                                                  .build();) {
//            String accessToken = keycloakInstance.tokenManager().getAccessToken().getToken();
//            return "Bearer " + accessToken;
//        } catch (Exception e) {
//            log.error("Access token receiving fails", e);
//        }
//        return null;
//    }
 
}
