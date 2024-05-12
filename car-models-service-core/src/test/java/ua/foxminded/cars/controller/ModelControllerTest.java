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
package ua.foxminded.cars.controller;

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

import static org.apache.http.client.methods.RequestBuilder.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    mockMvc.perform(put(MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andExpect(status().isOk());
  }

  @Test
  void updateModel_shouldReturn400_whenYearIsNegative() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc.perform(put(MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, NEGATIVE_YEAR)
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void deleteModelById_shouldReturn404_whenNoModelInDb() throws Exception {
    doThrow(ModelNotFoundException.class).when(modelService).deleteModelById(MODEL_ID);

    mockMvc.perform(MockMvcRequestBuilders.delete(MODEL_ID_PATH, MODEL_ID))
      .andExpect(status().isNotFound());
  }
}
