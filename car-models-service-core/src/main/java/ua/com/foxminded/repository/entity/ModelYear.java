package ua.com.foxminded.repository.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "model_years")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelYear {
  
  @Id
  @Column(name = "year_value")
  private Integer value;
  
  @OneToMany(mappedBy = "year")
  private Set<Model> models;
}
