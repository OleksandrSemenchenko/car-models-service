package ua.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.cars.repository.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, String> {

  void setCategoryModel(String categoryName, UUID modelId);
}
