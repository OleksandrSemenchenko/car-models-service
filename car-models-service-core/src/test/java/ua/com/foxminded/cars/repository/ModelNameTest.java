package ua.com.foxminded.cars.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.com.foxminded.cars.entity.ModelName;

@DataJpaTest
class ModelNameTest {
    
    public static final String MODEL_NAME = "A7";
    
    @Autowired
    private ModelNameRepository modelNameRepository;

    @Test
    void findByModelsModelNameName_ShouldReturnModelName() {
        Optional<ModelName> modelNameOptional = modelNameRepository.findByModelsModelNameName(MODEL_NAME);
        
        assertEquals(MODEL_NAME, modelNameOptional.get().getName());
    }
}
