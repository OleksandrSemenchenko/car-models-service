package ua.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static ua.com.foxminded.controller.ExceptionHandlerController.NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE;
import static ua.com.foxminded.service.ModelNameService.NO_MODEL_NAME;
import static ua.foxminded.cars.config.controller.CategoryControllerComponentTest.CATEGORY_NAME;
import static ua.foxminded.cars.config.controller.CategoryControllerComponentTest.CATEGORY_NAME_WITHOUT_RELATIONS;
import static ua.foxminded.cars.config.controller.CategoryControllerComponentTest.NEW_CATEGORY_NAME;
import static ua.foxminded.cars.controller.ModelNameControllerComponentTest.MODEL_NAME;
import static ua.foxminded.cars.controller.ModelNameControllerComponentTest.MODEL_NAME_WITHOUT_RELATIONS;
import static ua.foxminded.cars.controller.ModelNameControllerComponentTest.NEW_MODEL_NAME;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MODEL_NOT_FOUND;
import static ua.foxminded.cars.service.CategoryService.NO_CATEGORY;
import static ua.foxminded.cars.service.ManufacturerService.NO_MANUFACTURER;
import static ua.foxminded.cars.service.impls.ModelServiceImpl.MODEL_ALREADY_EXISTS;
import static ua.foxminded.cars.service.impls.ModelServiceImpl.NO_MODEL_WITH_SUCH_ID;
import static ua.foxminded.cars.service.impls.ModelServiceImpl.NO_SUCH_MODEL;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ua.com.foxminded.dto.ModelDto;

class ModelControllerComponentTest extends ComponentTestContext {

  private static final String V1 = "/v1";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final int MODEL_ID = 1;
  private static final int NEW_MODEL_ID = 2;
  private static final int MODEL_YEAR = 2020;
  private static final int NOT_EXISTED_MODEL_YEAR = 2021;
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MODEL_NAME = "A7";

  @Test
  void getByManufacturerAndNameAndYear_shouldReturnStatus400_whenYearIsNegative() {
    webTestClient
        .get()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, -2023)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .exists();
  }

  @Test
  void getByManufacturer_shouldReturnStatus404_WhenNoModelInDb() {
    webTestClient
        .get()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(
            String.format(MODEL_NOT_FOUND, MANUFACTURER_NAME, MODEL_NAME, NOT_EXISTED_MODEL_YEAR));
  }

  @Test
  void getModel_shouldReturnStatus200AndBody_whenModelIsInDb() {
    webTestClient
        .get()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(String.valueOf(MODEL_ID));
  }

  @Test
  void search_ShouldReturnStatus400_WhenMaxAndMinYearParametersAreNegative() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/v1/models")
                    .queryParam("model", MODEL_NAME)
                    .queryParam("category", CATEGORY_NAME)
                    .queryParam(
                        "manufacturer", ManufacturerControllerComponentTest.MANUFACTURER_NAME)
                    .queryParam("maxYear", -2025)
                    .queryParam("minYear", -2023)
                    .build())
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void search_ShouldReturnStatus200_WhenParametersExist() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/v1/models")
                    .queryParam("model", MODEL_NAME)
                    .queryParam("category", CATEGORY_NAME)
                    .queryParam(
                        "manufacturer", ManufacturerControllerComponentTest.MANUFACTURER_NAME)
                    .queryParam("maxYear", MODEL_YEAR)
                    .queryParam("minYear", MODEL_YEAR)
                    .build())
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .value(hasSize(1));
  }

  @Test
  void search_ShouldReturnStatus200_WhenNoParameters() {
    webTestClient
        .get()
        .uri("/v1/models")
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .value(hasSize(1));
  }

  @Test
  void getById_ShouldReturnStatus404_WhenNoSuchModel() {
    webTestClient
        .get()
        .uri("/v1/models/{id}", NEW_MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_MODEL_WITH_SUCH_ID, NEW_MODEL_ID));
  }

  @Test
  void getById_ShouldReturnStatus200() {
    webTestClient
        .get()
        .uri("/v1/models/{id}", MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo(MODEL_NAME);
  }

  @Test
  void create_ShouldReturnStatus400_WhenModelDtoCategoriesIsNull() {
    ModelDto modelDto = new ModelDto();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void create_ShouldReturnStatus400_WhenYearIsNegative() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(NEW_CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            -2021)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void create_ShouldReturnStatus404_WhenNoRequiredCategory() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(NEW_CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
  }

  @Test
  void create_ShouldReturnStatus404_WhenNoRequiredModelName() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            NEW_MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_MODEL_NAME, NEW_MODEL_NAME));
  }

  @Test
  void create_ShouldReturnStatus404_WhenNoRequiredManufacturer() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.NEW_MANUFACTURER_NAME,
            MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(
            String.format(
                NO_MANUFACTURER, ManufacturerControllerComponentTest.NEW_MANUFACTURER_NAME));
  }

  @Test
  void create_ShouldReturnStatus409_WhenSuchModelAlreadyExists() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(MODEL_ALREADY_EXISTS, MODEL_ID));
  }

  @Test
  void create_ShouldReturnStatus201() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME_WITHOUT_RELATIONS,
            MODEL_NAME_WITHOUT_RELATIONS,
            NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .value("Location", containsString(carModelServiceBaseUrl + "/v1/models/"));
  }

  @Test
  void update_ShouldReturnStatus400_WhenYearIsNegative() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .put()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            -2024)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void update_ShouldReturnStatus400_WhenModelDtoCategoriesIsNull() {
    ModelDto modelDto = new ModelDto();

    webTestClient
        .put()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE);
  }

  @Test
  void update_ShouldReturnStatus404_WhenNoRequiredCategory() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(NEW_CATEGORY_NAME)).build();

    webTestClient
        .put()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_CATEGORY, NEW_CATEGORY_NAME));
  }

  @Test
  void update_ShouldReturnStatus404_WhenNoSuchModel() {
    ModelDto modelDto = ModelDto.builder().categories(Set.of(CATEGORY_NAME)).build();

    webTestClient
        .put()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            NEW_MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(
            String.format(
                NO_SUCH_MODEL,
                ManufacturerControllerComponentTest.MANUFACTURER_NAME,
                NEW_MODEL_NAME,
                MODEL_YEAR));
  }

  @Test
  void update_ShouldReturnStatus200() {
    ModelDto modelDto =
        ModelDto.builder().categories(Set.of(CATEGORY_NAME_WITHOUT_RELATIONS)).build();

    webTestClient
        .put()
        .uri(
            "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
            ManufacturerControllerComponentTest.MANUFACTURER_NAME,
            MODEL_NAME,
            MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void deleleteById_ShouldReturnStatus404_WhenNoSuchModel() {
    webTestClient
        .delete()
        .uri("/v1/models/{id}", NEW_MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo(String.format(NO_MODEL_WITH_SUCH_ID, NEW_MODEL_ID));
  }

  @Test
  void deleteById_ShouldReturnStatus204() {
    webTestClient
        .delete()
        .uri("/v1/models/{id}", MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNoContent()
        .expectBody()
        .isEmpty();
  }
}
