package ua.foxminded.cars.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Sql("/db/category-test-data.sql")
@TestPropertySource("/application-it.properties")
class CategoryControllerIntegrationTest {

  private static final String V1 = "/v1";
  private static final String CATEGORIES_PATH = "/categories";
  private static final String CATEGORY_NAME = "Coupe";

  @MockBean private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired private MockMvc mockMvc;

  @Test
  void getAllCategories_shouldReturnStatus200AndBody_whenCategoriesAreInDb() throws Exception {
    mockMvc
        .perform(get(V1 + CATEGORIES_PATH).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name", Matchers.is(CATEGORY_NAME)));
  }
}
