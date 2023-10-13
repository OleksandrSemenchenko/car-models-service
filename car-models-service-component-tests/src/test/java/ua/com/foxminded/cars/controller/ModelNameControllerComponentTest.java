package ua.com.foxminded.cars.controller;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

class ModelNameControllerComponentTest extends ComponentTestContext {
    
    public static final String MODEL_NAME = "A7";
    public static final String MODEL_NAME_WITHOUT_REALTIONS = "A8";
    public static final String NOT_EXISTING_MODEL_NAME = "Mustang";
    
    @Test
    void getByName_ShouldReturnStatus200_WhenNoSuchModelName() {
        webTestClient.get().uri("/v1/model-names/{name}",  NOT_EXISTING_MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk();
    }
    
    @Test
    void getByName_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/model-names/{name}", MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.name").isEqualTo(MODEL_NAME);
    }
    
    @Test
    void getAll_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/model-names")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content", hasSize(2));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus404_WhenNoSuchModelName() {
        webTestClient.delete().uri("/v1/model-names/{name}", NOT_EXISTING_MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void deleteByName_ShouldReturnStatus204() {
        webTestClient.delete().uri("/v1/model-names/{name}", MODEL_NAME_WITHOUT_REALTIONS)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
