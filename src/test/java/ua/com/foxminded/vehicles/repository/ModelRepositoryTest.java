package ua.com.foxminded.vehicles.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;

import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entitymother.ModelMother;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ModelRepositoryTest {
    
    @Autowired
    private ModelRepository modelRepostory;
    
    private Model model;
    
    @BeforeTransaction
    void init() {
        model = ModelMother.complete().build();
        modelRepostory.saveAndFlush(model);
    }
    
    @Test
    void updateName_ShouldUpdateName() {
        String newName = "Q7";
        modelRepostory.updateName(newName, model.getName());
        Optional<Model> updatedModelOpt = modelRepostory.findById(newName);
        
        assertTrue(updatedModelOpt.isPresent());
    }
}
