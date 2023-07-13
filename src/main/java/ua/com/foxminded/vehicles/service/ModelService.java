package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.ModelModel;

public interface ModelService extends GenericService<ModelModel> {
    
    public void deleteByName(String name) throws ServiceException;
}
