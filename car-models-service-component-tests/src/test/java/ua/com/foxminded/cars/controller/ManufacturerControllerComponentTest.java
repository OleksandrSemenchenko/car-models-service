package ua.com.foxminded.cars.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.cars.testcontainer.Testcontainers;

@Slf4j
class ManufacturerControllerComponentTest extends Testcontainers {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    private static WebTestClient client;

    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        client = WebTestClient.bindToServer().baseUrl(carModelServiceBaseUrl).build();
        client.get().uri("/v1/manufacturers")
                    .header("Authorization", getAdminRoleBearerToken())
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().jsonPath("$.content[0].name")
                    .isEqualTo(MANUFACTURER_NAME);
    }
}
