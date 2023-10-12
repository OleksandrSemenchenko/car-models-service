package ua.com.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static ua.com.foxminded.cars.controller.CategoryControllerComponentTest.CATEGORY;
import static ua.com.foxminded.cars.controller.CategoryControllerComponentTest.CATEGORY_WITHOUT_RELATIONS;
import static ua.com.foxminded.cars.controller.ManufacturerControllerComponentTest.MANUFACTURER;
import static ua.com.foxminded.cars.controller.ManufacturerControllerComponentTest.MANUFACTURER_WITHOUT_RELATIONS;
import static ua.com.foxminded.cars.controller.ModelNameControllerComponentTest.MODEL_NAME;
import static ua.com.foxminded.cars.controller.ModelNameControllerComponentTest.MODEL_NAME_WITHOUT_REALTIONS;
import static ua.com.foxminded.cars.controller.ModelNameControllerComponentTest.NOT_EXISTING_MODEL_NAME;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import ua.com.foxminded.cars.dto.ModelDto;
import ua.com.foxminded.cars.testcontainer.ComponentContext;

class ModelControllerComponentTest extends ComponentContext {
    
    public static final int MODEL_ID = 1;
    public static final int NOT_EXISTING_MODEL_ID = 2;
    public static final int MODEL_YEAR = 2020;
    public static final int NOT_EXISTING_MODEL_YEAR = 2021;
    public static final int CREATED_MODEL_ID = 2;
    
    @Test
    void getByManufacturerAndNameAndYear_ShouldReturnStatus200_WhenNoSuchModel() {
        webTestClient.get().uri("/v1/manufacturers/{manufacturer}/models/{name}/{year}", 
                                MANUFACTURER, MODEL_NAME, NOT_EXISTING_MODEL_YEAR)
                           .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                           .exchange()
                           .expectStatus().isOk();
    }
    
    @Test
    void getByManufacturerAndNameAndYear_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/manufacturers/{manufacturer}/models/{name}/{year}", 
                                MANUFACTURER, MODEL_NAME, MODEL_YEAR)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content", hasSize(1));
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenParametersExist() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/v1/models")
                                                        .queryParam("model", MODEL_NAME)
                                                        .queryParam("category", CATEGORY)
                                                        .queryParam("manufacturer", MANUFACTURER)
                                                        .queryParam("maxYear", MODEL_YEAR)
                                                        .queryParam("minYear", MODEL_YEAR).build())
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content", Matchers.hasSize(1));
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenNoParameters() {
        webTestClient.get().uri("/v1/models")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content", Matchers.hasSize(1));
    }
    
    @Test
    void getById_ShouldReturnStatus409_WhenNoSuchModel() {
        webTestClient.get().uri("/v1/models/{id}", MODEL_ID)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.name").isEqualTo(MODEL_NAME);
    }
    
    @Test
    void getById_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/models/{id}", MODEL_ID)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.name").isEqualTo(MODEL_NAME);
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenModelAlreadyExists() {
        ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY)).build();
        webTestClient.post().uri("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                                 MANUFACTURER, MODEL_NAME, MODEL_YEAR)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    void save_ShouldReturnStatus201() {
        ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY)).build();
        webTestClient.post().uri("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                                 MANUFACTURER_WITHOUT_RELATIONS, 
                                 MODEL_NAME_WITHOUT_REALTIONS, 
                                 NOT_EXISTING_MODEL_YEAR)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().value("Location", containsString(carModelServiceBaseUrl + "/v1/models/"));
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoSuchModel() {
        ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY)).build();
        webTestClient.put().uri("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                                MANUFACTURER, NOT_EXISTING_MODEL_NAME, MODEL_YEAR)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isNotFound();
    }
    
    @Test
    void update_ShouldReturnStatus200() {
        ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_WITHOUT_RELATIONS)).build();
        webTestClient.put().uri("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                                MANUFACTURER, MODEL_NAME, MODEL_YEAR)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(modelDto)
                     .exchange()
                     .expectStatus().isOk();
    }
    
    @Test
    void deleleteById_ShouldReturnStatus404_WhenNoSuchModel() {
        webTestClient.delete().uri("/v1/models/{id}", NOT_EXISTING_MODEL_ID)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void deleteById_ShouldReturnStatus204() {
        webTestClient.delete().uri("/v1/models/{id}", MODEL_ID)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
