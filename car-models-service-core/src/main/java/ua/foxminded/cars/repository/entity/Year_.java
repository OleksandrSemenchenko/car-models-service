package ua.foxminded.cars.repository.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Year.class)
public class Year_ {

  public static volatile SingularAttribute<Year, Integer> value;
  public static volatile SetAttribute<Year, Model> models;
}
