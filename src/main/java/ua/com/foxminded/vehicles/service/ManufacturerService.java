package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.ManufacturerModel;

public interface ManufacturerService extends GenericService<ManufacturerModel> {
    
    public void deleteByName(String name) throws ServiceException;
}
