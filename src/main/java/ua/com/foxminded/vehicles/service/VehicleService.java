package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.VehicleModel;

public interface VehicleService extends GenericService<VehicleModel> {
    
    public void deleteById(String id) throws ServiceException;
}
