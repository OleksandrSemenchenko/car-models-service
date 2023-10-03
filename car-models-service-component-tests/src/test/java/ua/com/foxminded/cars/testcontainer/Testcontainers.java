package ua.com.foxminded.cars.testcontainer;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.cars.config.KeycloakConfig;
import ua.com.foxminded.cars.config.TestConfig;

@Slf4j
@SpringBootTest(classes = TestConfig.class)
public abstract class Testcontainers {
    
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    public static final String ADMIN_USER = "admin";
    public static final String CLIENT_SECRET = "secret";
    public static final String JWK_SET_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri";
    public static final String AUTH_SERVER_URL_PROPERTY = "keycloak.policy-enforcer-config.auth-server-url";
    
    public static final String DATABASE_ALIAS = "postgres_ip";
    public static final String AUTHORIZATION_SERVER_ALIAS = "keycloak_ip";
    
    private static Network network = Network.newNetwork();
    
    private static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile(REALM_CONFIG_FILE_PATH)
            .withExposedPorts(8080)
            .withNetwork(network)
            .withNetworkAliases(AUTHORIZATION_SERVER_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(log));
    
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("cars")
            .withUsername("cars")
            .withPassword("1234")
            .withNetwork(network)
            .withNetworkAliases(DATABASE_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(log));
    
    private  static GenericContainer<?> carsModelsService;
    
    static {
      keycloak.start();
      postgres.start();
      
      Map<String, String> variables = Map.of("POSTGRES_HOST", DATABASE_ALIAS, 
                                             "KEYCLOAK_HOST", AUTHORIZATION_SERVER_ALIAS);
      
      carsModelsService = new GenericContainer<>("car-models-service-core:latest")
              .withExposedPorts(8180)
              .withNetwork(network)
              .withEnv(variables)
              .dependsOn(postgres)
              .dependsOn(keycloak)
              .withLogConsumer(new Slf4jLogConsumer(log));

      carsModelsService.start();
    }
    
    public String carModelsSeviceAsdress = carsModelsService.getHost() + ":" + carsModelsService.getMappedPort(8180);
    public String carModelServiceBaseUrl = "http://" + carModelsSeviceAsdress;
    
    @Autowired
    private KeycloakConfig keycloakConfig;
    
    @BeforeEach
    void setUp() {
        var databaseDelegate = new JdbcDatabaseDelegate(postgres, "");
        ScriptUtils.runInitScript(databaseDelegate, "data.sql");
    }
    
    @AfterEach
    void cleanUp() {
        var databaseDelegate = new JdbcDatabaseDelegate(postgres, "");
        ScriptUtils.runInitScript(databaseDelegate, "data-clean-up.sql");
    }
    
    public String getAdminRoleBearerToken() {
        return getBearerToken(ADMIN_USER, ADMIN_USER);
    }
    
    private String getBearerToken(String username, String password) {
        System.out.println(keycloak.getHost());
        System.out.println(keycloak.getMappedPort(8080));
        System.out.println(keycloak.getAuthServerUrl());
        System.out.println(keycloakConfig.getRealm());
        System.out.println(keycloakConfig.getResource());
        System.out.println(keycloakConfig.getSecret());
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
}
