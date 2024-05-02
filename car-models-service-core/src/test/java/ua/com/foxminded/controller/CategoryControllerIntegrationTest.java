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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.service.dto.CategoryDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class CategoryControllerIntegrationTest {

  public static final String CATEGORY_NAME_WITHOUT_RELATIONS = "Coupe";
  public static final String NEW_CATEGORY_NAME = "SUV";
  public static final String CATEGORY_NAME = "Sedan";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  private CategoryDto categoryDto;

  @BeforeEach
  void setUp() {
    categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
  }

  @Test
  void create_ShouldReturnStatus201() throws Exception {
    categoryDto.setName(NEW_CATEGORY_NAME);
    String categoryDtoJson = mapper.writeValueAsString(categoryDto);

    mockMvc
        .perform(post("/v1/categories").contentType(APPLICATION_JSON).content(categoryDtoJson))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", containsString("/v1/categories/" + NEW_CATEGORY_NAME)));
  }

  @Test
  void getByName_ShouldReturnStatus200() throws Exception {
    mockMvc
        .perform(get("/v1/categories/{name}", CATEGORY_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$['name']", is(CATEGORY_NAME)));
  }

  @Test
  void getAll_ShouldReturnStatus200() throws Exception {
    mockMvc
        .perform(get("/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", Matchers.hasSize(2)));
  }

  @Test
  void deleteByName_ShouldReturnStatus204() throws Exception {
    mockMvc
        .perform(delete("/v1/categories/{name}", CATEGORY_NAME_WITHOUT_RELATIONS))
        .andExpect(status().isNoContent());
  }
}
