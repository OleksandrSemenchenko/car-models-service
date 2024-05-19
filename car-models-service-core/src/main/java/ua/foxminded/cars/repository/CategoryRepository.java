package ua.foxminded.cars.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.cars.repository.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

  void setCategoryModel(String categoryName, UUID modelId);
}
