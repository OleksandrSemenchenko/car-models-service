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

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "models")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID id;

  @NotNull private String name;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "model_year")
  private ModelYear year;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "manufacturer_name")
  private Manufacturer manufacturer;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "model_category",
      joinColumns = @JoinColumn(name = "model_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"))
  private Set<Category> categories;

  public void addCategories(Set<Category> categories) {
    for (Category category : categories) {
      addCategory(category);
    }
  }

  public void addCategory(Category category) {
    this.categories.add(category);
    category.getModels().add(this);
  }

  public void removeCategory(Category category) {
    this.categories.remove(category);
    category.getModels().remove(this);
  }
}
