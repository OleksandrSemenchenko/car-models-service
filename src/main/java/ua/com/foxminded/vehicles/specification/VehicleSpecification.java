package ua.com.foxminded.vehicles.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.SetJoin;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Category_;
import ua.com.foxminded.vehicles.entity.Manufacturer_;
import ua.com.foxminded.vehicles.entity.Model_;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.entity.Vehicle_;

public class VehicleSpecification {
    
    private VehicleSpecification() {
    }

    public static Specification<Vehicle> getSpecification(SearchFilter searchFilter) {
        return (vehicleRoot, vehicleQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (searchFilter.getManufacturer() != null) {
                predicates.add(criteriaBuilder.equal(vehicleRoot.get(Vehicle_.manufacturer).get(Manufacturer_.name), 
                                                     searchFilter.getManufacturer()));
            }
            
            if (searchFilter.getCategory() != null) {
                SetJoin<Vehicle, Category> category = vehicleRoot.join(Vehicle_.categories);
                predicates.add(criteriaBuilder.equal(category.get(Category_.name), searchFilter.getCategory()));
            }
            
            if (searchFilter.getMaxYear() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(vehicleRoot.get(Vehicle_.productionYear), 
                                                                 searchFilter.getMaxYear()));
            }
            
            if (searchFilter.getMinYear() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(vehicleRoot.get(Vehicle_.productionYear), 
                                                                    searchFilter.getMinYear()));
            }
            
            if (searchFilter.getModel() != null) {
                predicates.add(criteriaBuilder.equal(vehicleRoot.get(Vehicle_.model).get(Model_.name), 
                                                     searchFilter.getModel()));
            }
            
            if (searchFilter.getYear() != null) {
                predicates.add(criteriaBuilder.equal(vehicleRoot.get(Vehicle_.productionYear), searchFilter.getYear()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
