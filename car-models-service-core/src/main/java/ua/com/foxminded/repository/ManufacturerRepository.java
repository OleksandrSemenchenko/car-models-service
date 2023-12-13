package ua.com.foxminded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    
}
