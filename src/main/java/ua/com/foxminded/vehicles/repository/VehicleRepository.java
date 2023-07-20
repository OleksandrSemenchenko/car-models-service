package ua.com.foxminded.vehicles.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    
    Page<Vehicle> findByModelName(String modelName, Pageable pageable);
    
    Page<Vehicle> findByCategoriesName(String categoryName, Pageable pageable);
    
    Page<Vehicle> findByManufacturerNameAndProductionYearLessThanEqual(
            String manufacturerName, int year, Pageable pageable);
    
    Page<Vehicle> findByManufacturerNameAndProductionYearGreaterThanEqual(
            String manufacturerName, int year, Pageable pageable);
}
