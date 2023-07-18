package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.VehicleEntity;

public class VehicleEntityMother {
    
    public static final int PRODUCTION_YEAR = 2023;
    
    public static VehicleEntity.VehicleEntityBuilder complete() {
        return VehicleEntity.builder().productionYear(PRODUCTION_YEAR);
    }
}
