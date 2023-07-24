package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    
}
