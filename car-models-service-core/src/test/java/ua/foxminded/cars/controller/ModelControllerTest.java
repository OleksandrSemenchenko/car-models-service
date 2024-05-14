package ua.foxminded.cars.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Year;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelNotFoundException;
import ua.foxminded.cars.exceptionhandler.exceptions.PeriodNotValidException;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.ModelService;
import ua.foxminded.cars.service.dto.ModelDto;

@WebMvcTest(controllers = ModelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModelControllerTest {

  private static final String V1 = "/v1";
  private static final String MODEL_ID_PATH = "/models/{modelId}";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MODELS_PATH = "/models";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MODEL_NAME = "A7";
  private static final int YEAR = 2020;
  private static final int NEGATIVE_YEAR = -2020;
  private static final UUID MODEL_ID = UUID.fromString("52096834-48af-41d1-b422-93600eff629a");
  private static final Year MAX_YEAR = Year.of(2024);
  private static final Year MIN_YEAR = Year.of(2020);
  private static final String CATEGORY_NAME = "Sedan";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ModelService modelService;

  @Test
  void searchModels_shouldReturnStatus400_whenMaxYearIsBeforeMinYear() throws Exception {
    doThrow(PeriodNotValidException.class)
        .when(modelService)
        .searchModel(any(SearchFilter.class), any(Pageable.class));

    mockMvc
        .perform(
            get(V1 + MODELS_PATH)
                .param("maxYear", MIN_YEAR.toString())
                .param("minYear", MAX_YEAR.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void searchModels_shouldReturnStatus400_whenYearIsNegative() throws Exception {
    String negativeYear = "-2023";

    mockMvc
        .perform(
            get(V1 + MODELS_PATH)
                .param("maxYear", negativeYear)
                .param("minYear", negativeYear)
                .param("year", negativeYear)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getModelById_shouldReturnStatus404_whenModelIsInDb() throws Exception {
    doThrow(ModelNotFoundException.class).when(modelService).getModelById(any(UUID.class));

    mockMvc
        .perform(get(V1 + MODEL_ID_PATH, MODEL_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void createModel_shouldReturnStatus409_whenModelAlreadyInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    doThrow(ModelAlreadyExistsException.class).when(modelService).createModel(isA(ModelDto.class));

    mockMvc
        .perform(
            post(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isConflict());
  }

  @Test
  void createModel_shouldReturnStatus400_whenNoCategoryInRequestBody() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    modelDto.setCategories(null);
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            post(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateModel_shouldReturnStatus404_whenNoModelInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    doThrow(ModelNotFoundException.class).when(modelService).updateModel(modelDto);

    mockMvc
        .perform(
            put(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void updateModel_shouldReturn400_whenYearIsNegative() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, NEGATIVE_YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteModelById_shouldReturn404_whenNoModelInDb() throws Exception {
    doThrow(ModelNotFoundException.class).when(modelService).deleteModelById(MODEL_ID);

    mockMvc
        .perform(MockMvcRequestBuilders.delete(V1 + MODEL_ID_PATH, MODEL_ID))
        .andExpect(status().isNotFound());
  }
}
