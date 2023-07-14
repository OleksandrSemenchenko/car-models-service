package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.model.Model;

public interface ModelService extends GenericService<Model> {
    
    public void deleteByName(String name);
    
    public Model getByName(String name);
    
    public Model updateName(String newName, String oldName);
}
