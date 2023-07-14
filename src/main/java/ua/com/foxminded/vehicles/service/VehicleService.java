package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.model.Vehicle;

public interface VehicleService extends GenericService<Vehicle> {
    
    public void deleteById(String id);
    
    public Vehicle getById(String id);
    
    public Vehicle update(Vehicle model);
}
