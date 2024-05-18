package ua.foxminded.cars;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.repository.specification.ModelSpecification;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.dto.ModelDto;

public class TestDataGenerator {

  private static final UUID MODEL_ID = UUID.fromString("2bd84edb-70aa-4e74-9a41-c0e962fd36db");
  private static final int YEAR = 2021;
  private static final String MODEL_NAME = "x6";
  private static final String MANUFACTURER_NAME = "BMW";
  private static final String CATEGORY_NAME = "Pickup";

  public static Category generateCategoryEntity() {
    return Category.builder().name(CATEGORY_NAME).build();
  }

  public static Specification<Model> generateSpecification() {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(MANUFACTURER_NAME).name(MODEL_NAME).year(YEAR).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  public static ModelDto generateModelDtoWithId() {
    ModelDto modelDto = generateModelDto();
    modelDto.setId(MODEL_ID);
    return modelDto;
  }

  public static ModelDto generateModelDto() {
    return ModelDto.builder()
        .year(YEAR)
        .manufacturer(MANUFACTURER_NAME)
        .name(MODEL_NAME)
        .categories(List.of(CATEGORY_NAME))
        .build();
  }

  public static Model generateModelEntityWithId() {
    return generateModelEntity(MODEL_ID);
  }

  public static Model generateModelEntity(UUID modelId) {
    ModelYear modelYear = ModelYear.builder().value(Year.of(YEAR)).build();
    Category category = Category.builder().name(CATEGORY_NAME).build();
    Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
    return Model.builder()
        .id(modelId)
        .year(modelYear)
        .manufacturer(manufacturer)
        .name(MODEL_NAME)
        .categories(new HashSet<>(List.of(category)))
        .build();
  }
}
