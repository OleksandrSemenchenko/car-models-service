package ua.foxminded.cars.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "model_years")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Year {

  @Id
  @Column(name = "year_value")
  private Integer value;

  @OneToMany(mappedBy = "year")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Model> models;
}
