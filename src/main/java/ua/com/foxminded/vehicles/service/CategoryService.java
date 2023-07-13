package ua.com.foxminded.vehicles.service;

import ua.com.foxminded.vehicles.model.CategoryModel;

public interface CategoryService extends GenericService<CategoryModel> {
    
    public void deleleteByName(String name);
}
