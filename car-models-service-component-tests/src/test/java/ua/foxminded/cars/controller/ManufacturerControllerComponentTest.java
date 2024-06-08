package ua.foxminded.cars.controller;

import static org.hamcrest.Matchers.hasSize;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MANUFACTURER_NOT_FOUND;

import org.junit.jupiter.api.Test;

class ManufacturerControllerComponentTest extends ComponentTestContext {

  private static final String V1 = "/v1";
  private static final String MANUFACTURER_PATH = "/manufacturers/{manufacturer}";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MANUFACTURER_NAME_WITHOUT_RELATIONS = "Ford";
  private static final String NEW_MANUFACTURER_NAME = "BMW";

  @Test
  void getByName_shouldReturnStatus404_whenNoManufacturerInDb() {
    webTestClient
        .get()
        .uri(V1 + MANUFACTURER_PATH, NEW_MANUFACTURER_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(MANUFACTURER_NOT_FOUND, NEW_MANUFACTURER_NAME));
  }

  @Test
  void getByName_shouldReturnStatus200AndBody_whenManufacturerIsInDb() {
    webTestClient
        .get()
        .uri(V1 + MANUFACTURER_PATH, MANUFACTURER_NAME)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$['name']")
        .isEqualTo(MANUFACTURER_NAME);
  }

  @Test
  void getAll_shouldReturnStatus200AndBody_whenManufacturersAreInDb() throws Exception {
    webTestClient
        .get()
        .uri("/v1/manufacturers")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .value(hasSize(2));
  }
}
