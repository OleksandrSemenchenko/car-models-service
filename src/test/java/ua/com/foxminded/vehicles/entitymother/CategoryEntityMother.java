package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.CategoryEntity;

public class CategoryEntityMother {
    
    public static final String CATEGORY_NAME = "SUV";
    
    public static CategoryEntity.CategoryEntityBuilder complete() {
        return CategoryEntity.builder().name(CATEGORY_NAME);
    }
}
