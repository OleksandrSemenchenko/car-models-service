package ua.com.foxminded.cars.controller;

import static org.hamcrest.Matchers.hasSize;
import static ua.com.foxminded.service.ModelNameService.MODEL_NAME_ALREADY_EXISTS;
import static ua.com.foxminded.service.ModelNameService.MODEL_NAME_DATABASE_CONSTRAINT;
import static ua.com.foxminded.service.ModelNameService.NO_MODEL_NAME;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import ua.com.foxminded.dto.ModelDto;

class ModelNameControllerComponentTest extends ComponentTestContext {
    
    public static final String MODEL_NAME = "A7";
    public static final String MODEL_NAME_WITHOUT_RELATIONS = "A8";
    public static final String NEW_MODEL_NAME = "Mustang";
    
    @Test
    void save_ShouldReturn409_WhenSuchModelNameAlreadyExists() {
        ModelDto modelDto = ModelDto.builder().name(MODEL_NAME).build();
        webTestClient.post().uri("/v1/model-names")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(MODEL_NAME_ALREADY_EXISTS, MODEL_NAME));
    }
    
    @Test
    void save_ShouldReturn201() {
        ModelDto modelDto = ModelDto.builder().name(NEW_MODEL_NAME).build();
        webTestClient.post().uri("/v1/model-names")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().location(carModelServiceBaseUrl + "/v1/model-names/" + NEW_MODEL_NAME); 
    }
    
    @Test
    void getByName_ShouldReturnStatus404_WhenNoSuchModelName() {
        webTestClient.get().uri("/v1/model-names/{name}",  NEW_MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(NO_MODEL_NAME, NEW_MODEL_NAME));
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
                     .expectBody().jsonPath("$.content").value(hasSize(2));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus405_WhenModelNameHasDatabaseRelations() {
        webTestClient.delete().uri("/v1/model-names/{name}", MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(MODEL_NAME_DATABASE_CONSTRAINT, MODEL_NAME));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus404_WhenNoSuchModelName() {
        webTestClient.delete().uri("/v1/model-names/{name}", NEW_MODEL_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(NO_MODEL_NAME, NEW_MODEL_NAME));
    }

    @Test
    void deleteByName_ShouldReturnStatus204() {
        webTestClient.delete().uri("/v1/model-names/{name}", MODEL_NAME_WITHOUT_RELATIONS)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
