package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.model.Category;

public interface CategoryService extends GenericService<Category> {
    
    public void deleleteByName(String name);
    
    public Category getByName(String name);
    
    public Category updateName(String newName, String oldName);
}
