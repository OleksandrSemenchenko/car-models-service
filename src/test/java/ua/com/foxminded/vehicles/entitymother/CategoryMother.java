package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.Category;

public class CategoryMother {
    
    public static final String CATEGORY_NAME = "SUV";
    
    public static Category.CategoryBuilder complete() {
        return Category.builder().name(CATEGORY_NAME);
    }
}
