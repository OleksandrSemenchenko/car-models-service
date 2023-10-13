package ua.com.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.cars.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    
    Manufacturer findByModelsManufacturerName(String name);
}
