package ua.com.foxminded.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.config.TestConfig;
import ua.com.foxminded.testcontainer.KeycloakTestcontainer;

@Slf4j
@SpringBootTest(classes = TestConfig.class)
public abstract class ComponentTestContext {
    
    public static final String REALM_CONFIG_FILE_PATH = "/realm-import.json";
    public static final String ADMIN_USER = "admin";
    public static final String ADMIN_PASSWORD = "admin";
    public static final String DATABASE_ALIAS = "postgres";
    public static final String AUTHORIZATION_SERVER_ALIAS = "keycloak";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CLIENT_SECRET = "secret";
    
    private static Network network = Network.newNetwork();
    private static KeycloakTestcontainer keycloak;
    private static PostgreSQLContainer<?> postgres;
    private static GenericContainer<?> carsModelsService;
    
    static {
        keycloak = new KeycloakTestcontainer()
                .withRealmImportFile(REALM_CONFIG_FILE_PATH)
                .withNetworkAliases(AUTHORIZATION_SERVER_ALIAS)
                .withContextPath("/auth")
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
        
        carsModelsService = new GenericContainer<>("foxminded/car-models-service-core:latest")
                .withExposedPorts(8180)
                .withNetwork(network)
                .withEnv("KEYCLOAK_HOST", AUTHORIZATION_SERVER_ALIAS)
                .withEnv("POSTGRES_HOST", DATABASE_ALIAS)
                .dependsOn(postgres)
                .dependsOn(keycloak)
                .withLogConsumer(new Slf4jLogConsumer(log));
        carsModelsService.start();
    }
    
    public WebTestClient webTestClient;
    public String carModelServiceBaseUrl;
    
    @Autowired
    private PolicyEnforcerConfig policyEnforcerConfig;
    
    @Value("${build.majorVersion}")
    private String projectMajorVersion;
    
    @BeforeEach
    void setUp() {
        carModelServiceBaseUrl = "http://" + carsModelsService.getHost() + ":" + 
                carsModelsService.getMappedPort(8180) + "/v" + projectMajorVersion;
        webTestClient = WebTestClient.bindToServer().baseUrl(carModelServiceBaseUrl).build();
        var databaseDelegate = new JdbcDatabaseDelegate(postgres, "");
        ScriptUtils.runInitScript(databaseDelegate, "data.sql");
    }
    
    @AfterEach
    void cleanUp() {
        var databaseDelegate = new JdbcDatabaseDelegate(postgres, "");
        ScriptUtils.runInitScript(databaseDelegate, "data-clean-up.sql");
    }
    
    public String getAdminRoleBearerToken() {
        return getBearerToken(ADMIN_USER, ADMIN_PASSWORD);
    }
    
    private String getBearerToken(String username, String password) {
        Keycloak keycloakInstance = Keycloak.getInstance(keycloak.getAuthServerUrl(),
                                                         policyEnforcerConfig.getRealm(),
                                                         username,
                                                         password,
                                                         policyEnforcerConfig.getResource(),
                                                         policyEnforcerConfig.getCredentials()
                                                                             .get(CLIENT_SECRET).toString());
        
        String accessToken = keycloakInstance.tokenManager().getAccessTokenString();
        return "Bearer " + accessToken;
    }
}
