package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.Model;

public interface ModelRepository extends JpaRepository<Model, String> {
    
}
