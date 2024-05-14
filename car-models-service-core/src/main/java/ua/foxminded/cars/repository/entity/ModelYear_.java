package ua.foxminded.cars.repository.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Year;

@StaticMetamodel(ModelYear.class)
public class ModelYear_ {

  public static volatile SingularAttribute<ModelYear, Year> value;
  public static volatile SetAttribute<ModelYear, Model> models;
}
