package ua.com.foxminded.vehicles.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ua.com.foxminded.vehicles.model.Vehicle;

public interface VehicleService extends GenericService<Vehicle> {
    
    public Page<Vehicle> getByModel(String modelName, Pageable pageable);
    
    public Page<Vehicle> getByCategory(String categoryName, Pageable pageable);
    
    public Page<Vehicle> getByManufacturerNameAndMinYear(
            String manufacturerName, int minYear, Pageable pageable);
    
    public Page<Vehicle> getByManufacturerNameAndMaxYear(
            String manufacturerName, int maxYear, Pageable pageable);
    
    public void deleteById(String id);
    
    public Vehicle getById(String id);
    
    public Vehicle update(Vehicle model);
}
