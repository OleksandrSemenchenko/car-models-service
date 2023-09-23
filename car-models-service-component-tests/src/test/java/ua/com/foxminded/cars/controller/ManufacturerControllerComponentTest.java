package ua.com.foxminded.cars.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import ua.com.foxminded.cars.TestConfig;
import ua.com.foxminded.cars.config.KeycloakConfig;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = TestConfig.class)
class ManufacturerControllerComponentTest extends TestContainers {

    public static final String MANUFACTURER_NAME = "Mazda";
    
    private static WebTestClient client;
    
    @BeforeAll
    public static void init() {
        String baseUrl = "http://" + carModelSeviceAsdress;
        client = WebTestClient.bindToServer().baseUrl(baseUrl).build();
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        client.get().uri("/v1/manufacturers")
//                    .header("Authorization", getAdminRoleBearerToken())
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().jsonPath("$.content[0].name").isEqualTo(MANUFACTURER_NAME);
    }

}
