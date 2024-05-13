package ua.foxminded.cars.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.specification.ModelSpecification;
import ua.foxminded.cars.repository.specification.SearchFilter;

@DataJpaTest
@Sql(scripts = "/model-test-data.sql")
class ModelRepositoryTest {

  private static final String MODEL = "A7";
  private static final String MANUFACTURE = "Audi";
  private static final String NOT_EXISTING_MANUFACTURER = "Volvo";
  private static final Integer MODEL_YEAR = 2020;
  private static final Integer NOT_EXISTING_MODEL_YEAR = 2035;
  private static final String CATEGORY = "Sedan";
  private static final String CATEGORY_WITHOUT_RELATIONS = "Coupe";
  private static final String NOT_EXISTING_CATEGORY = "Pickup";
  private static final UUID MODEL_ID = UUID.fromString("52096834-48af-41d1-b422-93600eff629a");
  private static final UUID NOT_EXISTING_MODEL_ID = UUID.randomUUID();

  @Autowired private ModelRepository modelRepository;

  @Test
  void removeModelFromCategory_shouldRemoveRelationship_whenRelationshipExists() {
    modelRepository.removeModelFromCategory(MODEL_ID, CATEGORY);

    boolean isExistByCategory = modelRepository.existsByCategoriesName(CATEGORY);

    assertFalse(isExistByCategory);
  }

  @Test
  void putModelToCategory_shouldThrowException_whenNoModelAndCategoryInDB() {
    assertThrows(
        DataIntegrityViolationException.class,
        () -> modelRepository.putModelToCategory(NOT_EXISTING_MODEL_ID, NOT_EXISTING_CATEGORY));
  }

  @Test
  void putModelToCategory_shouldCreateRelationship_whenCategoryAndModelAreInDb() {
    modelRepository.putModelToCategory(MODEL_ID, CATEGORY_WITHOUT_RELATIONS);

    boolean isExistByCategory = modelRepository.existsByCategoriesName(CATEGORY_WITHOUT_RELATIONS);

    assertTrue(isExistByCategory);
  }

  @Test
  void existsByYearValue_shouldReturnTrue_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByYearValue(NOT_EXISTING_MODEL_YEAR);

    assertFalse(isModelExist);
  }

  @Test
  void existsByYearValue_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByYearValue(MODEL_YEAR);

    assertTrue(isModelExist);
  }

  @Test
  void existsByCategoriesName_shouldReturnTrue_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByCategoriesName(NOT_EXISTING_CATEGORY);

    assertFalse(isModelExist);
  }

  @Test
  void existsByCategoriesName_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByCategoriesName(CATEGORY);

    assertTrue(isModelExist);
  }

  @Test
  void existsByManufacturer_Name_shouldReturnFalse_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByManufacturerName(NOT_EXISTING_MANUFACTURER);

    assertFalse(isModelExist);
  }

  @Test
  void existsByManufacturer_Name_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByManufacturerName(MANUFACTURE);

    assertTrue(isModelExist);
  }

  @Test
  void findOne_shouldReturnEmptyOptional_whenModelIsInDb() {
    SearchFilter filter =
        SearchFilter.builder()
            .name(MODEL)
            .manufacturer(NOT_EXISTING_MANUFACTURER)
            .year(MODEL_YEAR)
            .build();
    Specification<Model> specification = ModelSpecification.getSpecification(filter);

    Optional<Model> modelOptional = modelRepository.findOne(specification);

    assertTrue(modelOptional.isEmpty());
  }

  @Test
  void findOne_shouldReturnModel_whenModelIsInDb() {
    SearchFilter filter =
        SearchFilter.builder().name(MODEL).manufacturer(MANUFACTURE).year(MODEL_YEAR).build();
    Specification<Model> specification = ModelSpecification.getSpecification(filter);

    Model model = modelRepository.findOne(specification).get();

    assertEquals(MODEL, model.getName());
    assertEquals(MANUFACTURE, model.getManufacturer().getName());
    assertEquals(MODEL_YEAR, model.getYear().getValue());
  }
}
