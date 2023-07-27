package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    
}
