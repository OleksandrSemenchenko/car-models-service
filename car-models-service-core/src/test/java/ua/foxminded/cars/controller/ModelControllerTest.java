package ua.foxminded.cars.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelNotFoundException;
import ua.foxminded.cars.service.dto.ModelDto;
import ua.foxminded.cars.service.imp.ModelServiceImp;

@WebMvcTest(controllers = ModelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModelControllerTest {

  private static final String MODEL_ID_PATH = "/v1/models/{modelId}";
  private static final String MODEL_PATH = "/v1/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MODEL_NAME = "A7";
  private static final int YEAR = 2020;
  private static final int NEGATIVE_YEAR = -2020;
  private static final UUID MODEL_ID = UUID.fromString("52096834-48af-41d1-b422-93600eff629a");

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ModelServiceImp modelService;

  @Test
  void updateModel_shouldReturnStatus404_whenNoModelInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    doThrow(ModelNotFoundException.class).when(modelService).updateModel(modelDto);

    mockMvc
        .perform(
            put(MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
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
            put(MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, NEGATIVE_YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteModelById_shouldReturn404_whenNoModelInDb() throws Exception {
    doThrow(ModelNotFoundException.class).when(modelService).deleteModelById(MODEL_ID);

    mockMvc
        .perform(MockMvcRequestBuilders.delete(MODEL_ID_PATH, MODEL_ID))
        .andExpect(status().isNotFound());
  }
}
