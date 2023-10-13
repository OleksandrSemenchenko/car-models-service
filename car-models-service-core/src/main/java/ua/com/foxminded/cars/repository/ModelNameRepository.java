package ua.com.foxminded.cars.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.cars.entity.ModelName;

public interface ModelNameRepository extends JpaRepository<ModelName, String> {
    
    Optional<ModelName> findByModelsModelNameName(String name);
}
