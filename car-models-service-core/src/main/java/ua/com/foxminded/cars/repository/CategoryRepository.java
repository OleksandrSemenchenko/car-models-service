package ua.com.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.cars.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    
}
