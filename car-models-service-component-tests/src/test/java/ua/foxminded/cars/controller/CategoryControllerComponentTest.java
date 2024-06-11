package ua.foxminded.cars.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class CategoryControllerComponentTest extends ComponentTestContext {

  @Test
  void getAllCategories_shouldReturnStatus200AndBody_whenCategoriesAreInDb() {
    webTestClient
        .get()
        .uri("/v1/categories")
        .header("Authorization", getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[*].name")
        .value(Matchers.hasItem("Sedan"))
        .jsonPath("$.content[*].name")
        .value(Matchers.hasItem("Coupe"));
  }
}
