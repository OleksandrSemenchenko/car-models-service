package ua.com.foxminded.cars.controller;

import static org.keycloak.test.FluentTestsHelper.DEFAULT_KEYCLOAK_URL;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.test.FluentTestsHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class ControllerIntegrationTest {
    
    public static final String CATEGORY_NAME = "Sedan";
    public static final String CAR_SERVICES_REALM = "car-services";
    public static final String CAR_MODELS_SERVICE_CLIENT = "car-models-service";
    public static final String USER_NAME_ADMIN = "admin";
    public static final String CLIENT_SECRET = "1093tvvCAW172tdZxQm7gvnA1MIHcsL0";
    
    protected static FluentTestsHelper keycloak;
    
    @BeforeAll
    @SuppressWarnings("resource")
    public static void onBeforeClass() throws IOException {
        try {
            keycloak = new FluentTestsHelper().init().importTestRealm("/realm-export.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @AfterAll
    public static void cleanUp() throws IOException {
        keycloak.deleteTestRealm();
        keycloak.close();
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
        Keycloak keycloak = Keycloak.getInstance(
                DEFAULT_KEYCLOAK_URL,
                CAR_SERVICES_REALM,
                username,
                password,
                CAR_MODELS_SERVICE_CLIENT,
                CLIENT_SECRET);
        return keycloak.tokenManager().getAccessTokenString();
    }
}
