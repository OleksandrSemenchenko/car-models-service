package ua.com.foxminded.repository;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;

@DataJpaTest
class ModelRepositoryTest {
  
  private final String MODEL_NAME = "A7";
  private final String MANUFACTURE_NAME = "Audi";
  private final Integer MODEL_YEAR = 2020;
  
  @Autowired
  private ModelRepository modelRepository;
  
  @Test
  void findByNameYearManufacturerName_shouldReturnModel_whenModelIsInDb() {
    SearchFilter filter = SearchFilter.builder()
        .name(MODEL_NAME)
        .manufacturer(MANUFACTURE_NAME)
        .year(MODEL_YEAR).build();
    Specification<Model> specification = ModelSpecification.getSpecification(filter);
    
    Model model = modelRepository.findOne(specification).get();
    
    assertEquals(MODEL_NAME, model.getName());
    assertEquals(MANUFACTURE_NAME, model.getManufacturer().getName());
    assertEquals(MODEL_YEAR, model.getYear().getValue());
  }
}
