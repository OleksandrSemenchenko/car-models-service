package ua.com.foxminded.cars.testcontainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.cars.config.KeycloakConfig;
import ua.com.foxminded.cars.config.TestConfig;

@Slf4j
@SpringBootTest(classes = TestConfig.class)
public abstract class ComponentContext {
    
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    public static final String ADMIN_USER = "admin";
    public static final String CLIENT_SECRET = "secret";
    public static final String JWK_SET_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri";
    public static final String AUTH_SERVER_URL_PROPERTY = "keycloak.policy-enforcer-config.auth-server-url";
    public static final int KEYCLOAK_PORT_HTTP = 8080;
    
    public static final String DATABASE_ALIAS = "postgres";
    public static final String AUTHORIZATION_SERVER_ALIAS = "keycloak";
    
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    private static Network network = Network.newNetwork();
    
    public static KeycloakTestcontainer keycloak;
    public static PostgreSQLContainer<?> postgres;
    public static GenericContainer<?> carsModelsService;
    public static WebTestClient webTestClient;
    public static String carModelsSeviceAddress;
    public static String carModelServiceBaseUrl;
    
    static {
        keycloak = new KeycloakTestcontainer()
                .withRealmImportFile(REALM_CONFIG_FILE_PATH)
                .withNetworkAliases(AUTHORIZATION_SERVER_ALIAS)
                .withContextPath("/auth")
                .withExposedPorts(8080)
                .withNetwork(network)
                .withLogConsumer(new Slf4jLogConsumer(log));
        keycloak.start();
        
        postgres = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("cars")
                .withUsername("cars")
                .withPassword("cars")
                .withNetwork(network)
                .withNetworkAliases(DATABASE_ALIAS)
                .withLogConsumer(new Slf4jLogConsumer(log));
        postgres.start();
        
        carsModelsService = new GenericContainer<>("cars/car-models-service-core:latest")
                .withExposedPorts(8180)
                .withNetwork(network)
                .withEnv("KEYCLOAK_HOST", AUTHORIZATION_SERVER_ALIAS)
                .withEnv("POSTGRES_HOST", DATABASE_ALIAS)
                .dependsOn(postgres)
                .dependsOn(keycloak)
                .withLogConsumer(new Slf4jLogConsumer(log));
        carsModelsService.start();
        
        carModelsSeviceAddress = carsModelsService.getHost() + ":" + carsModelsService.getMappedPort(8180);
        carModelServiceBaseUrl = "http://" + carModelsSeviceAddress;
        webTestClient = WebTestClient.bindToServer().baseUrl(carModelServiceBaseUrl).build();
    }
    
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
