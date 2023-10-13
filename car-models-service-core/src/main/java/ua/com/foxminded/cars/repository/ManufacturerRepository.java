package ua.com.foxminded.cars.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.cars.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    
    Optional<Manufacturer> findByModelsManufacturerName(String name);
}
