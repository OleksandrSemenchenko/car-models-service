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

import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entitymother.CategoryMother;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryRepositoryTest {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Category category;
    
    @BeforeTransaction
    void init() {
        category = CategoryMother.complete().build();
        categoryRepository.saveAndFlush(category);
    }

    @Test
    void updateName_ShouldUpdateName() {
        String newName = "Sedan";
        categoryRepository.updateName(newName, category.getName());
        Optional<Category> categoryOpt = categoryRepository.findById(newName);
        
        assertTrue(categoryOpt.isPresent());
    }
}
