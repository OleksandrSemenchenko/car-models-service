package ua.com.foxminded.vehicles.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Manufacturer.class)
public class Manufacturer_ {
    
    public static volatile SingularAttribute<Manufacturer, String> name;
    public static volatile SetAttribute<Manufacturer, Vehicle> vehicles;
}
