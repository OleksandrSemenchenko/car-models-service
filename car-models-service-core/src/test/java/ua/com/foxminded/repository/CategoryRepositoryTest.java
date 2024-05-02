package ua.com.foxminded.repository;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.com.foxminded.repository.entity.Category;

@DataJpaTest
public class CategoryRepositoryTest {
  
  private final String MODEL_NAME = "A7";
  
  @Autowired
  private CategoryRepository categoryRepository;
  
//  @Test
//  void findByModelName_shouldReturnCategories_whenCategoriesAreInDb() {
//    
//    List<Category> categories = categoryRepository.findByModelsName_Name(MODEL_NAME);
//    assertEquals(1, categories.size());
//  }
}
