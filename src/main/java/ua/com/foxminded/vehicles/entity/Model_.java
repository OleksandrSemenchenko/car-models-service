package ua.com.foxminded.vehicles.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Model.class)
public class Model_ {
    
    public static volatile SingularAttribute<Model, String> name;
    public static volatile SetAttribute<Model, Vehicle> vehicles;
}
