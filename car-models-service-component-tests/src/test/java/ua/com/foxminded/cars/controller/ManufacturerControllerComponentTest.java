package ua.com.foxminded.cars.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import ua.com.foxminded.cars.dto.ManufacturerDto;

class ManufacturerControllerComponentTest extends ComponentTestContext {
    
    public static final String MANUFACTURER_NAME = "Audi";
    public static final String MANUFACTURER_NAME_WITHOUT_RELATIONS = "Ford";
    public static final String NEW_MANUFACTURER_NAME = "BMW";
    
    @Test
    void getByName_ShouldReturnStatus200_WhenNoSuchManufacturer() {
        webTestClient.get().uri("/v1/manufacturers/{manufacturer}", NEW_MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk();
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
                     .expectBody().jsonPath("$.content", hasSize(2));
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenManufacturerAlreadyExists() throws JsonProcessingException {
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
        
        webTestClient.post().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .contentType(APPLICATION_JSON)
                     .bodyValue(manufacturerDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    void save_ShouldReturnStatus201() throws JsonProcessingException {
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name("Chevrolet").build();
        
        webTestClient.post().uri("/v1/manufacturers")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .contentType(APPLICATION_JSON)
                     .bodyValue(manufacturerDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().location(carModelServiceBaseUrl + "/v1/manufacturers/" + 
                                              manufacturerDto.getName());
    }
    
    @Test
    void delete_ShouldReturnStatus405_WhenManufacturerHasDatabaseConstraints() {
        webTestClient.delete().uri("/v1/manufacturers/{manufacturer}", MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @Test
    void delete_ShouldReturnStatus404_WhenNoManufacturer() {
        webTestClient.delete().uri("/v1/manufacturers/{manufacturer}", NEW_MANUFACTURER_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound();
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
