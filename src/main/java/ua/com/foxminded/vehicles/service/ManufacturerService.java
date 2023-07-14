package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.model.Manufacturer;

public interface ManufacturerService extends GenericService<Manufacturer> {
    
    public void deleteByName(String name);
    
    public Manufacturer getByName(String name);
    
    public Manufacturer updateName(String newName, String name);
}
