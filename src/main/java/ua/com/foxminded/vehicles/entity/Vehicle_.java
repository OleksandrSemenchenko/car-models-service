package ua.com.foxminded.vehicles.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Vehicle.class)
public class Vehicle_ {
    
    public static volatile SingularAttribute<Vehicle, String> id;
    public static volatile SingularAttribute<Vehicle, Integer> productionYear;
    public static volatile SingularAttribute<Vehicle, Manufacturer> manufacturer;
    public static volatile SingularAttribute<Vehicle, Model> model;
    public static volatile SetAttribute<Vehicle, Category> categories;
}
