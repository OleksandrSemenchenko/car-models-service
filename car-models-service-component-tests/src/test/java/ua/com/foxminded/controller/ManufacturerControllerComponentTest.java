package ua.com.foxminded.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ua.com.foxminded.controller.ExceptionHandlerController.DATA_INTEGRRITY_VIOLATION_EXCEPTION_MESSAGE;
import static ua.com.foxminded.controller.ExceptionHandlerController.NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE;
import static ua.com.foxminded.service.ManufacturerService.MANUFACTURER_ALREADY_EXISTS;
import static ua.com.foxminded.service.ManufacturerService.NO_MANUFACTURER;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import ua.com.foxminded.dto.ManufacturerDto;

class ManufacturerControllerComponentTest extends ComponentTestContext {
    
    public static final String MANUFACTURER_NAME = "Audi";
    public static final String MANUFACTURER_NAME_WITHOUT_RELATIONS = "Ford";
    public static final String NEW_MANUFACTURER_NAME = "BMW";
    
    @Test
    void getByName_ShouldReturnStatus404_WhenNoSuchManufacturer() {
        webTestClient.get().uri("/v1/manufacturers/{manufacturer}", NEW_MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(NO_MANUFACTURER, NEW_MANUFACTURER_NAME));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/manufacturers/{manufacturer}", MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$['name']").isEqualTo(MANUFACTURER_NAME);
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        webTestClient.get().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content").value(hasSize(2));
    }
    
    @Test
    void create_ShouldReturnStatus400_WhenManufacturerDtoNameIsNull() {
        ManufacturerDto manufacturerDto = new ManufacturerDto();
        
        webTestClient.post().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .contentType(APPLICATION_JSON)
                     .bodyValue(manufacturerDto)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody().jsonPath("$.message").isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
    }
    
    @Test
    void create_ShouldReturnStatus409_WhenManufacturerAlreadyExists() throws JsonProcessingException {
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
        
        webTestClient.post().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .contentType(APPLICATION_JSON)
                     .bodyValue(manufacturerDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(MANUFACTURER_ALREADY_EXISTS, MANUFACTURER_NAME));
    }
    
    @Test
    void create_ShouldReturnStatus201() throws JsonProcessingException {
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name(NEW_MANUFACTURER_NAME).build();
        
        webTestClient.post().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .contentType(APPLICATION_JSON)
                     .bodyValue(manufacturerDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().location(carModelServiceBaseUrl + "/v1/manufacturers/" + NEW_MANUFACTURER_NAME);
    }
    
    @Test
    void delete_ShouldReturnStatus405_WhenManufacturerHasRelations() {
        webTestClient.delete().uri("/v1/manufacturers/{manufacturer}", MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(DATA_INTEGRRITY_VIOLATION_EXCEPTION_MESSAGE, MANUFACTURER_NAME));
    }
    
    @Test
    void delete_ShouldReturnStatus404_WhenNoManufacturer() {
        webTestClient.delete().uri("/v1/manufacturers/{manufacturer}", NEW_MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(NO_MANUFACTURER, 
                                                                                 NEW_MANUFACTURER_NAME));
    }
    
    @Test
    void delete_ShouldReturnStatus204_WhenManufacturerExists() {
        webTestClient.delete().uri("/v1/manufacturers/{manufacturer}", MANUFACTURER_NAME_WITHOUT_RELATIONS)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
