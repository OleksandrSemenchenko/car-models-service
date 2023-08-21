package ua.com.foxminded.vehicles.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Model.class)
public class Model_ {
    
    public static volatile SingularAttribute<Model, String> id;
    public static volatile SingularAttribute<Model, Integer> year;
    public static volatile SingularAttribute<Model, Manufacturer> manufacturer;
    public static volatile SingularAttribute<Model, ModelName> modelName;
    public static volatile SetAttribute<Model, Category> categories;
}
