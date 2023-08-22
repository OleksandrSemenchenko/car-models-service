package ua.com.foxminded.cars.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Category.class)
public class Category_ {
    
    public static volatile SingularAttribute<Category, String> name;
    public static volatile SetAttribute<Category, Model> models;
}
