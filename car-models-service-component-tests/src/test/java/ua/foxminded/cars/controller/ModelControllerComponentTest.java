package ua.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MODEL_ALREADY_EXIST_BY_PARAMETERS;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MODEL_NOT_FOUND;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MODEL_NOT_FOUND_BY_ID;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ua.foxminded.cars.service.dto.ModelDto;

class ModelControllerComponentTest extends ComponentTestContext {

  private static final String V1 = "/v1";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MODEL_ID_PATH = "/models/{modelId}";
  private static final String MODELS_PATH = "/models";
  private static final String MODEL_ID = "52096834-48af-41d1-b422-93600eff629a";
  private static final String NOT_EXISTING_MODEL_ID = "6fb2687b-7e2d-4730-acc1-a0607de0a444";
  private static final int MODEL_YEAR = 2020;
  private static final int MAX_YEAR = 2021;
  private static final int MIN_YEAR = 2018;
  private static final int NOT_EXISTED_MODEL_YEAR = 2021;
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MODEL_NAME = "A7";
  private static final String NEW_CATEGORY = "Coupe";
  private static final String CATEGORY_NAME = "Sedan";
  private static final String NOT_EXISTING_MODEL_NAME = "A15";
  private static final String NEW_MANUFACTURER = "Ford";
  private static final String AUTHORIZATION_HEADER = "Authorization";

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
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
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
        .jsonPath("$.details")
        .isEqualTo(MODEL_NOT_FOUND.formatted(MANUFACTURER_NAME, MODEL_NAME, NOT_EXISTED_MODEL_YEAR))
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(404);
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
  void searchModel_shouldReturnStatus400_whenMaxAndMinYearParametersAreNegative() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(V1 + MODELS_PATH)
                    .queryParam("model", MODEL_NAME)
                    .queryParam("category", CATEGORY_NAME)
                    .queryParam("manufacturer", MANUFACTURER_NAME)
                    .queryParam("maxYear", -2025)
                    .queryParam("minYear", -2023)
                    .build())
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
  }

  @Test
  void searchModel_shouldReturnStatus200AndBody_whenRequestHasParameters() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(V1 + MODELS_PATH)
                    .queryParam("name", MODEL_NAME)
                    .queryParam("category", CATEGORY_NAME)
                    .queryParam("manufacturer", MANUFACTURER_NAME)
                    .queryParam("maxYear", MAX_YEAR)
                    .queryParam("minYear", MIN_YEAR)
                    .build())
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[*].id")
        .value(Matchers.hasItem(MODEL_ID));
  }

  @Test
  void searchModel_shouldReturnStatus200AndBody_whenNoRequestParameters() {
    webTestClient
        .get()
        .uri(V1 + MODELS_PATH)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .value(hasSize(1));
  }

  @Test
  void getModelById_shouldReturnStatus404_whenNoModelInDb() {
    webTestClient
        .get()
        .uri(V1 + MODEL_ID_PATH, NOT_EXISTING_MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.details")
        .isEqualTo(MODEL_NOT_FOUND_BY_ID.formatted(NOT_EXISTING_MODEL_ID))
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(404);
  }

  @Test
  void getModelById_shouldReturnStatus200_whenModelIsInDb() {
    webTestClient
        .get()
        .uri(V1 + MODEL_ID_PATH, MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo(MODEL_NAME);
  }

  @Test
  void createModel_shouldReturnStatus400_whenNoCategoriesInRequestBody() {
    ModelDto modelDto = new ModelDto();

    webTestClient
        .post()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
  }

  @Test
  void createModel_shouldReturnStatus400_whenYearIsNegative() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(NEW_CATEGORY)).build();

    webTestClient
        .post()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, -2021)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
  }

  @Test
  void createModel_shouldReturnStatus409AndErrorBody_whenModelAlreadyExists() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.details")
        .isEqualTo(
            MODEL_ALREADY_EXIST_BY_PARAMETERS.formatted(
                MANUFACTURER_NAME, MODEL_NAME, MODEL_YEAR, MODEL_ID))
        .jsonPath("$.timestamp")
        .hasJsonPath()
        .jsonPath("$.errorCode")
        .isEqualTo(409);
  }

  @Test
  void createModel_shouldReturnStatus201_whenNoModelInDb() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(CATEGORY_NAME)).build();

    webTestClient
        .post()
        .uri(V1 + MODEL_PATH, NEW_MANUFACTURER, MODEL_NAME, NOT_EXISTED_MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .value("Location", containsString(carModelServiceBaseUrl + "/v1/models/"));
  }

  @Test
  void updateModel_shouldReturnStatus400_whenYearIsNegative() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(CATEGORY_NAME)).build();

    webTestClient
        .put()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, -2024)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .isNotEmpty()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
  }

  @Test
  void updateModel_shouldReturnStatus400_whenNoCategoriesInRequestBody() {
    ModelDto modelDto = new ModelDto();

    webTestClient
        .put()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.details")
        .exists()
        .jsonPath("$.timestamp")
        .isNotEmpty()
        .jsonPath("$.errorCode")
        .isEqualTo(400);
  }

  @Test
  void updateModel_shouldReturnStatus404_whenNoModelInDb() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(CATEGORY_NAME)).build();

    webTestClient
        .put()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, NOT_EXISTING_MODEL_NAME, MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.details")
        .isEqualTo(
            String.format(MODEL_NOT_FOUND, MANUFACTURER_NAME, NOT_EXISTING_MODEL_NAME, MODEL_YEAR))
        .jsonPath("$.timestamp")
        .isNotEmpty()
        .jsonPath("$.errorCode")
        .isEqualTo(404);
  }

  @Test
  void updateModel_shouldReturnStatus200_whenModelIsInDb() {
    ModelDto modelDto = ModelDto.builder().categories(List.of(NEW_CATEGORY)).build();

    webTestClient
        .put()
        .uri(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, MODEL_YEAR)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .bodyValue(modelDto)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void deleteModelById_shouldReturnStatus404_whenNoModelInDb() {
    webTestClient
        .delete()
        .uri(V1 + MODEL_ID_PATH, NOT_EXISTING_MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.details")
        .isEqualTo(String.format(MODEL_NOT_FOUND_BY_ID, NOT_EXISTING_MODEL_ID))
        .jsonPath("$.timestamp")
        .isNotEmpty()
        .jsonPath("$.errorCode")
        .isEqualTo(404);
  }

  @Test
  void deleteModelById_shouldReturnStatus204_whenModelIsInDb() {
    webTestClient
        .delete()
        .uri(V1 + MODEL_ID_PATH, MODEL_ID)
        .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
        .exchange()
        .expectStatus()
        .isNoContent()
        .expectBody()
        .isEmpty();
  }
}
