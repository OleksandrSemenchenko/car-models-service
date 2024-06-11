package ua.foxminded.cars.controller;

import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MANUFACTURER_NOT_FOUND;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ManufacturerControllerComponentTest extends ComponentTestContext {

  private static final String V1 = "/v1";
  private static final String MANUFACTURER_PATH = "/manufacturers/{manufacturer}";
  private static final String MANUFACTURERS_PATH = "/manufacturers";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String NEW_MANUFACTURER_NAME = "BMW";
  private static final String AUTHORIZATION_HEADER = "Authorization";

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
        .jsonPath("$.details")
        .isEqualTo(MANUFACTURER_NOT_FOUND.formatted(NEW_MANUFACTURER_NAME))
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(404);
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
        .jsonPath("$.name")
        .isEqualTo(MANUFACTURER_NAME);
  }

  @Test
  void getAll_shouldReturnStatus200AndBody_whenManufacturersAreInDb() {
    webTestClient
        .get()
        .uri(V1 + MANUFACTURERS_PATH)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[*].name")
        .value(Matchers.hasItem("Audi"))
        .jsonPath("$.content[*].name")
        .value(Matchers.hasItem("Ford"));
  }
}
