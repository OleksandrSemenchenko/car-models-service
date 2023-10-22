package ua.com.foxminded.controller;

import static org.hamcrest.Matchers.hasSize;
import static ua.com.foxminded.controller.ExceptionHandlerController.DATA_INTEGRITY_VIOLATION_MESSAGE;
import static ua.com.foxminded.service.CategoryService.CATEGORY_ALREADY_EXISTS;
import static ua.com.foxminded.service.CategoryService.NO_CATEGORY;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import ua.com.foxminded.dto.CategoryDto;


class CategoryControllerComponentTest extends ComponentTestContext {
    
    public static final String CATEGORY_NAME_WITHOUT_RELATIONS = "Coupe";
    public static final String CATEGORY_NAME = "Sedan";
    public static final String NEW_CATEGORY_NAME = "Pickup";
    
    @Test
    void save_ShouldReturnStatus409_WhenSuchCategoryAlreadyExists() {
        CategoryDto categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
        
        webTestClient.post().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(categoryDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().jsonPath("$.message").isEqualTo(
                             String.format(CATEGORY_ALREADY_EXISTS, CATEGORY_NAME));
    }
    
    @Test
    void save_ShouldReturnStatus201() {
        CategoryDto categoryDto = CategoryDto.builder().name(NEW_CATEGORY_NAME).build();
        
        webTestClient.post().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(categoryDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().location(carModelServiceBaseUrl + "/v1/categories/" + NEW_CATEGORY_NAME);
    }
    
    @Test
    void getByName_ShoudReturnStatus404_WhenNoSuchCategory() {
        webTestClient.get().uri("/v1/categories/{category}", NEW_CATEGORY_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/categories/{category}", CATEGORY_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.name").isEqualTo(CATEGORY_NAME);
    }
    
    @Test
    void getAll_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content").value(hasSize(2));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus405_WhenCategoryHasRelations() {
        webTestClient.delete().uri("/v1/categories/{category}", CATEGORY_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(DATA_INTEGRITY_VIOLATION_MESSAGE, 
                                                                                 CATEGORY_NAME));
    }
    
    @Test
    void deleleteByName_ShouldReturnStatus404_WhenNoSuchCategory() {
        webTestClient.delete().uri("/v1/categories/{category}", NEW_CATEGORY_NAME)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().jsonPath("$.message").isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
    }

    @Test
    void deleteByName_ShouldReturnStatus204() {
        webTestClient.delete().uri("/v1/categories/{category}", CATEGORY_NAME_WITHOUT_RELATIONS)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
