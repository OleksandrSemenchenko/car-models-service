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
package ua.com.foxminded.repository.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Category_;
import ua.com.foxminded.repository.entity.Manufacturer_;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.entity.ModelYear_;
import ua.com.foxminded.repository.entity.Model_;

public class ModelSpecification {

  private ModelSpecification() {}

  public static Specification<Model> getSpecification(SearchFilter searchFilter) {
    return (modelRoot, modelQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (searchFilter.getManufacturer() != null) {
        predicates.add(
            criteriaBuilder.equal(
                modelRoot.get(Model_.manufacturer).get(Manufacturer_.name),
                searchFilter.getManufacturer()));
      }

      if (searchFilter.getCategory() != null) {
        SetJoin<Model, Category> category = modelRoot.join(Model_.categories);
        predicates.add(
            criteriaBuilder.equal(category.get(Category_.name), searchFilter.getCategory()));
      }

      if (searchFilter.getMaxYear() != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(
                modelRoot.get(Model_.year).get(ModelYear_.value), searchFilter.getMaxYear()));
      }

      if (searchFilter.getMinYear() != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(
                modelRoot.get(Model_.year).get(ModelYear_.value), searchFilter.getMinYear()));
      }

      if (searchFilter.getName() != null) {
        predicates.add(criteriaBuilder.equal(modelRoot.get(Model_.name), searchFilter.getName()));
      }

      if (searchFilter.getYear() != null) {
        predicates.add(
            criteriaBuilder.equal(
                modelRoot.get(Model_.year).get(ModelYear_.value), searchFilter.getYear()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
