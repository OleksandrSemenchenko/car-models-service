package ua.com.foxminded.vehicles.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.VehicleEntity;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {
    
    Page<VehicleEntity> findByModelName(String modelName, Pageable pageable);
    
    Page<VehicleEntity> findByCategoriesName(String categoryName, Pageable pageable);
    
    Page<VehicleEntity> findByManufacturerNameAndProductionYearLessThanEqual(
            String manufacturerName, int year, Pageable pageable);
    
    Page<VehicleEntity> findByManufacturerNameAndProductionYearGreaterThanEqual(
            String manufacturerName, int year, Pageable pageable);
}
