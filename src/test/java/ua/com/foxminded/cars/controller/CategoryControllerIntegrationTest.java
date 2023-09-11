package ua.com.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.keycloak.test.FluentTestsHelper.*;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.TestsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.CategoryDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerIntegrationTest {
    
    public static final String CATEGORY_NAME = "Sedan";
    public static final String CAR_SERVICES_REALM = "car-services";
    public static final String CAR_MODELS_SERVICE_CLIENT = "car-moedels-service";
    
    private static FluentTestsHelper keycloak;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private CategoryDto categoryDto;
    
    @BeforeAll
    @SuppressWarnings("resource")
    public static void onBeforeClass() throws IOException {
        try {
            keycloak = new FluentTestsHelper().init().importTestRealm("/realm-export.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @AfterAll
    public static void cleanUp() throws IOException {
        keycloak.deleteTestRealm();
        keycloak.close();
    }

    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
//    @WithMockUser
    void save_ShouldReturnStatus201() throws Exception {
        String newCategoryName = "SUV";
        categoryDto.setName(newCategoryName);
        String categoryDtoJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson)
                                              .with(csrf()))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/categories/" + newCategoryName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/categories/{name}", CATEGORY_NAME))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/categories").with(bearerTokenFor("test")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    @WithMockUser
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/categories/{name}", CATEGORY_NAME).with(csrf()))
               .andExpect(status().isNoContent());
    }
    
    private RequestPostProcessor bearerTokenFor(String username) {
        String token = getToken(username, username);

        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.addHeader("Authorization", "Bearer " + token);
                return request;
            }
        };
    }
    
    public String getToken(String username, String password) {
        Keycloak keycloak = Keycloak.getInstance(
                DEFAULT_KEYCLOAK_URL,
                CAR_SERVICES_REALM,
                username,
                password,
                CAR_MODELS_SERVICE_CLIENT,
                "1093tvvCAW172tdZxQm7gvnA1MIHcsL0");
        return keycloak.tokenManager().getAccessTokenString();
    }
}
