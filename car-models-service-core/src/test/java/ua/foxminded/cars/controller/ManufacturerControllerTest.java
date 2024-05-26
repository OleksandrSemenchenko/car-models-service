package ua.foxminded.cars.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.service.ManufacturerService;

@WebMvcTest(ManufacturerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ManufacturerControllerTest {

  private static final String V1 = "/v1";
  private static final String MANUFACTURER_PATH = "/manufacturers/{name}";
  private static final String MANUFACTURER_NAME = "Audi";

  @Autowired private MockMvc mockMvc;

  @MockBean private ManufacturerService manufacturerService;

  @Test
  void getManufacturer_shouldReturnStatus404AndExceptionBody_whenNoManufacturerInDb()
      throws Exception {
    Mockito.when(manufacturerService.getManufacturer(any(String.class)))
        .thenThrow(ManufacturerNotFoundException.class);

    mockMvc
        .perform(get(V1 + MANUFACTURER_PATH, MANUFACTURER_NAME))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.details").hasJsonPath())
        .andExpect(jsonPath("$.errorCode").value(404))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
