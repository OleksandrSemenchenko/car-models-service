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
package ua.com.foxminded.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.exceptionhandler.exceptions.AlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.NotFoundException;
import ua.com.foxminded.service.ManufacturerService;
import ua.com.foxminded.service.dto.ManufacturerDto;

@WebMvcTest(ManufacturerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ManufacturerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @MockBean private ManufacturerService manufacturerService;

  private ManufacturerDto manufacturerDto;
  private String manufacturerDtoJson;

  @BeforeEach
  void setUp() {
    manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
  }

  @Test
  void create_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
    manufacturerDto.setName(null);
    manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);

    mockMvc
        .perform(
            post("/v1/manufacturers").contentType(APPLICATION_JSON).content(manufacturerDtoJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
    manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
    doThrow(AlreadyExistsException.class).when(manufacturerService).create(manufacturerDto);

    mockMvc
        .perform(
            post("/v1/manufacturers").contentType(APPLICATION_JSON).content(manufacturerDtoJson))
        .andExpect(status().isConflict());
  }

  @Test
  void deleteByName_ShouldReturnStatus405_WhenDataIntegrityViolationException() throws Exception {
    doThrow(DataIntegrityViolationException.class)
        .when(manufacturerService)
        .deleteByName(MANUFACTURER_NAME);

    mockMvc
        .perform(delete("/v1/manufacturers/{name}", MANUFACTURER_NAME))
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  void deleteByName_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
    doThrow(NotFoundException.class).when(manufacturerService).deleteByName(MANUFACTURER_NAME);

    mockMvc
        .perform(delete("/v1/manufacturers/{name}", MANUFACTURER_NAME))
        .andExpect(status().isNotFound());
  }
}
