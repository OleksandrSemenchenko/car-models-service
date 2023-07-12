package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.com.foxminded.vehicles.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

}
