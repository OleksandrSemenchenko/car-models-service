/*
 * Copyright 2023 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.com.foxminded.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "models")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Model {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;

  @ToString.Include
  @JoinColumn(name = "model_year")
  @Column(name = "model_year")
  private Integer year;

  @ManyToOne
  @JoinColumn(name = "manufacturer_name")
  private Manufacturer manufacturer;

  @ManyToOne
  @JoinColumn(name = "name")
  private ModelName modelName;

  @ManyToMany
  @JoinTable(
      name = "model_category",
      joinColumns = @JoinColumn(name = "model_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"))
  private Set<Category> categories;

  public void addCategory(Category category) {
    this.categories.add(category);
    category.getModels().add(this);
  }

  public void removeCategory(Category category) {
    this.categories.remove(category);
    category.getModels().remove(this);
  }
}
