package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.ModelEntity;

public class ModelEntityMother {
    
    public static final String  MODEL_NAME = "Q3";
    
    public static ModelEntity.ModelEntityBuilder complete() {
        return ModelEntity.builder().name(MODEL_NAME);
    }
}
