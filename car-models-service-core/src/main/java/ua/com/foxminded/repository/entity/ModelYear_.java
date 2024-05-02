package ua.com.foxminded.repository.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ModelYear.class)
public class ModelYear_ {
  
  public static volatile SingularAttribute<ModelYear, Integer> value;
  public static volatile SetAttribute<ModelYear, Model> models;
}
