package ua.com.foxminded.vehicles.entitymother;

import ua.com.foxminded.vehicles.entity.Vehicle;

public class VehicleMother {
    
    public static final int PRODUCTION_YEAR = 2023;
    
    public static Vehicle.VehicleBuilder complete() {
        return Vehicle.builder().productionYear(PRODUCTION_YEAR);
    }
}
