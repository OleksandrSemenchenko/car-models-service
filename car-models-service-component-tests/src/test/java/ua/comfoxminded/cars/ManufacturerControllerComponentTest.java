package ua.comfoxminded.cars;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ManufacturerControllerComponentTest {
    
    @Container
    private static GenericContainer<?> carModelsService = new GenericContainer<>("docker-build:latest");
    
    
    
    

    @Test
    void test() {
        fail("Not yet implemented");
    }

}
