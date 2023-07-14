package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.ManufacturerEntity;

public class ManufacturerEntityMother {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    public static ManufacturerEntity.ManufacturerEntityBuilder complete() {
        return ManufacturerEntity.builder().name(MANUFACTURER_NAME);
    }
}
