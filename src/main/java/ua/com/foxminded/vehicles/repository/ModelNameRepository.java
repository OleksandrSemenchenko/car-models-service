package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.ModelName;

public interface ModelNameRepository extends JpaRepository<ModelName, String> {
    
}
