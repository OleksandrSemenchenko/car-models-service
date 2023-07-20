package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.Manufacturer;

public class ManufacturerMother {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    public static Manufacturer.ManufacturerBuilder complete() {
        return Manufacturer.builder().name(MANUFACTURER_NAME);
    }
}
