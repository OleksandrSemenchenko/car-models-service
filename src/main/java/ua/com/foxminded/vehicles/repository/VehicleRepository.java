package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.VehicleEntity;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {

}
