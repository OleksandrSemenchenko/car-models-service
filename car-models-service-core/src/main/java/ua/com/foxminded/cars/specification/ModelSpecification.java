package ua.com.foxminded.cars.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.SetJoin;
import ua.com.foxminded.cars.entity.Category;
import ua.com.foxminded.cars.entity.Category_;
import ua.com.foxminded.cars.entity.Manufacturer_;
import ua.com.foxminded.cars.entity.Model;
import ua.com.foxminded.cars.entity.ModelName_;
import ua.com.foxminded.cars.entity.Model_;

public class ModelSpecification {
    
    private ModelSpecification() {
    }

    public static Specification<Model> getSpecification(SearchFilter searchFilter) {
        return (modelRoot, modelQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (searchFilter.getManufacturer() != null) {
                predicates.add(criteriaBuilder.equal(modelRoot.get(Model_.manufacturer).get(Manufacturer_.name), 
                                                     searchFilter.getManufacturer()));
            }
            
            if (searchFilter.getCategory() != null) {
                SetJoin<Model, Category> category = modelRoot.join(Model_.categories);
                predicates.add(criteriaBuilder.equal(category.get(Category_.name), searchFilter.getCategory()));
            }
            
            if (searchFilter.getMaxYear() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(modelRoot.get(Model_.year), 
                                                                 searchFilter.getMaxYear()));
            }
            
            if (searchFilter.getMinYear() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(modelRoot.get(Model_.year), 
                                                                    searchFilter.getMinYear()));
            }
            
            if (searchFilter.getModel() != null) {
                predicates.add(criteriaBuilder.equal(modelRoot.get(Model_.modelName).get(ModelName_.name), 
                                                     searchFilter.getModel()));
            }
            
            if (searchFilter.getYear() != null) {
                predicates.add(criteriaBuilder.equal(modelRoot.get(Model_.year), searchFilter.getYear()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
