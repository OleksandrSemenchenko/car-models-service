/*
 * Copyright 2023 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.foxminded.controller;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ua.foxminded.cars.service.dto.CategoryDto;

class CategoryControllerComponentTest extends ComponentTestContext {

  public static final String CATEGORY_NAME_WITHOUT_RELATIONS = "Coupe";
  public static final String CATEGORY_NAME = "Sedan";
  public static final String NEW_CATEGORY_NAME = "Pickup";

  @Test
  void create_ShouldReturnStatus400_WhenCategoryDtoNameIsNull() {
    CategoryDto categoryDto = new CategoryDto();

    webTestClient
        .post()
        .uri("/v1/categories")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(categoryDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void create_ShouldReturnStatus409_WhenSuchCategoryAlreadyExists() {
    CategoryDto categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();

    webTestClient
        .post()
        .uri("/v1/categories")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(categoryDto)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(CATEGORY_ALREADY_EXISTS, CATEGORY_NAME));
  }

  @Test
  void create_ShouldReturnStatus201() {
    CategoryDto categoryDto = CategoryDto.builder().name(NEW_CATEGORY_NAME).build();

    webTestClient
        .post()
        .uri("/v1/categories")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(categoryDto)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .location(carModelServiceBaseUrl + "/v1/categories/" + NEW_CATEGORY_NAME);
  }

  @Test
  void getByName_ShoudReturnStatus404_WhenNoSuchCategory() {
    webTestClient
        .get()
        .uri("/v1/categories/{category}", NEW_CATEGORY_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
  }

  @Test
  void getByName_ShouldReturnStatus200() {
    webTestClient
        .get()
        .uri("/v1/categories/{category}", CATEGORY_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo(CATEGORY_NAME);
  }

  @Test
  void getAll_ShouldReturnStatus200() {
    webTestClient
        .get()
        .uri("/v1/categories")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .value(hasSize(2));
  }

  @Test
  void deleteByName_ShouldReturnStatus405_WhenCategoryHasRelations() {
    webTestClient
        .delete()
        .uri("/v1/categories/{category}", CATEGORY_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, CATEGORY_NAME));
  }

  @Test
  void deleleteByName_ShouldReturnStatus404_WhenNoSuchCategory() {
    webTestClient
        .delete()
        .uri("/v1/categories/{category}", NEW_CATEGORY_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
  }

  @Test
  void deleteByName_ShouldReturnStatus204() {
    webTestClient
        .delete()
        .uri("/v1/categories/{category}", CATEGORY_NAME_WITHOUT_RELATIONS)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNoContent()
        .expectBody()
        .isEmpty();
  }
}
