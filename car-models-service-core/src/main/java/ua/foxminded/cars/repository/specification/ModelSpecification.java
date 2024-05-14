package ua.foxminded.cars.repository.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.SetJoin;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.repository.entity.Category_;
import ua.foxminded.cars.repository.entity.Manufacturer_;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.entity.ModelYear_;
import ua.foxminded.cars.repository.entity.Model_;

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
        Year maxYear = Year.of(searchFilter.getMaxYear());
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(
                modelRoot.get(Model_.year).get(ModelYear_.value), maxYear));
      }

      if (searchFilter.getMinYear() != null) {
        Year minYear = Year.of(searchFilter.getMinYear());
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(
                modelRoot.get(Model_.year).get(ModelYear_.value), minYear));
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
