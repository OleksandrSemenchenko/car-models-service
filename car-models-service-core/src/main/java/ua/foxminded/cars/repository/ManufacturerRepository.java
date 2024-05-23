package ua.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {}
