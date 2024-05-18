package ua.foxminded.cars.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Year;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "model_years")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelYear {

  @Id
  @Column(name = "year_value")
  @JdbcTypeCode(SqlTypes.INTEGER)
  private Year year;

  @OneToMany(mappedBy = "year")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Model> models;
}
