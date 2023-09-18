package ua.com.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.cars.entity.ModelName;

public interface ModelNameRepository extends JpaRepository<ModelName, String> {
    
}
