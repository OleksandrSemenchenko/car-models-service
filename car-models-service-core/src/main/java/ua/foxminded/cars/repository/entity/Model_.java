package ua.foxminded.cars.repository.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Model.class)
public class Model_ {

  public static volatile SingularAttribute<Model, String> id;
  public static volatile SingularAttribute<Model, String> name;
  public static volatile SingularAttribute<Model, Year> year;
  public static volatile SingularAttribute<Model, Manufacturer> manufacturer;
  public static volatile SetAttribute<Model, Category> categories;
}
