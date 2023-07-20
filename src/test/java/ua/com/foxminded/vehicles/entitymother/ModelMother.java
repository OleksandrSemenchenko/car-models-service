package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.Model;

public class ModelMother {
    
    public static final String  MODEL_NAME = "Q3";
    
    public static Model.ModelBuilder complete() {
        return Model.builder().name(MODEL_NAME);
    }
}
