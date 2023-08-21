package ua.com.foxminded.cars.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ModelName.class)
public class ModelName_ {
    
    public static volatile SingularAttribute<ModelName, String> name;
    public static volatile SetAttribute<ModelName, Model> models;
}
