package ua.foxminded.cars.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Sql("/db/manufacturer-test-data.sql")
@TestPropertySource("/application-it.properties")
class ManufacturerControllerIntegrationTest {

  private static final String V1 = "/v1";
  private static final String MANUFACTURER_PATH = "/manufacturers/{name}";
  private static final String MANUFACTURERS_PATH = "/manufacturers";
  private static final String MANUFACTURER_NAME = "Audi";

  @MockBean private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired private MockMvc mockMvc;

  @Test
  void getAllManufacturers_shouldReturnStatus200AndPage_whenManufacturersAreInDb()
      throws Exception {
    mockMvc
        .perform(get(V1 + MANUFACTURERS_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name", is(MANUFACTURER_NAME)));
  }

  @Test
  void getManufacturer_shouldReturnStatus200AndBody_whenManufacturerIsInDb() throws Exception {
    mockMvc
        .perform(get(V1 + MANUFACTURER_PATH, MANUFACTURER_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(MANUFACTURER_NAME)));
  }
}
