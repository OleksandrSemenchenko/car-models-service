package ua.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.service.dto.ModelDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Sql(scripts = "/model-test-data.sql")
@Transactional
class ModelControllerIntegrationTest {

  private static final String V1 = "/v1";
  private static final String MODEL_ID_PATH = "/models/{modelId}";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MODELS_PATH = "/models";
  private static final int YEAR = 2020;
  private static final int NEW_YEAR = 2024;
  private static final String MODEL_NAME = "A7";
  private static final String NEW_MODEL_NAME = "Q8";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String NEW_MANUFACTURER_NAME = "Audi";
  private static final String MODEL_ID = "52096834-48af-41d1-b422-93600eff629a";
  private static final String CATEGORY_NAME = "Sedan";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void getModel_shouldReturnStatus200AndBody_whenModelIsInDb() throws Exception {
    mockMvc
        .perform(get(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name", is(MODEL_NAME)))
        .andExpect(jsonPath("$.manufacturer", is(MANUFACTURER_NAME)))
        .andExpect(jsonPath("$.year", is(YEAR)))
        .andExpect(jsonPath("$.categories[0]", is(CATEGORY_NAME)));
  }

  @Test
  void searchModels_shouldReturnPageAndStatus200_whenModelIsInDb() throws Exception {
    mockMvc
        .perform(get(V1 + MODELS_PATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").exists())
        .andExpect(jsonPath("$.content[0].name", is(MODEL_NAME)))
        .andExpect(jsonPath("$.content[0].manufacturer", is(MANUFACTURER_NAME)))
        .andExpect(jsonPath("$.content[0].year", is(YEAR)))
        .andExpect(jsonPath("$.content[0].categories[0]", is(CATEGORY_NAME)));
  }

  @Test
  void getModelById_shouldReturnBodyAndStatus200_whenModelIsInDb() throws Exception {
    mockMvc
        .perform(get(V1 + MODEL_ID_PATH, MODEL_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name", is(MODEL_NAME)))
        .andExpect(jsonPath("$.manufacturer", is(MANUFACTURER_NAME)))
        .andExpect(jsonPath("$.year", is(YEAR)))
        .andExpect(jsonPath("$.categories[0]", is(CATEGORY_NAME)));
  }

  @Test
  void createModel_shouldReturnStatus200_whenNoModelInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            post(V1 + MODEL_PATH, NEW_MANUFACTURER_NAME, NEW_MODEL_NAME, NEW_YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString(V1 + "/models/")));
  }

  @Test
  void updateModel_shouldReturnStatus200_whenModelIsInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(V1 + MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void deleteModelById_shouldReturn204_whenModelIsInDb() throws Exception {
    mockMvc.perform(delete(V1 + MODEL_ID_PATH, MODEL_ID)).andExpect(status().isNoContent());
  }
}
