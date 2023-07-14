package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.ManufacturerEntity;

public class ManufacturerEntityMother {
    
    public static final String NAME = "Audi";
    
    public static ManufacturerEntity.ManufacturerEntityBuilder complete() {
        return ManufacturerEntity.builder().name(NAME);
    }
}
