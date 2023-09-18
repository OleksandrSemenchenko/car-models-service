package ua.com.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ua.com.foxminded.cars.entity.Model;

public interface ModelRepository extends JpaRepository<Model, String>, JpaSpecificationExecutor<Model> {
    
}
