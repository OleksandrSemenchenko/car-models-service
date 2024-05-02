package ua.com.foxminded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ua.com.foxminded.repository.entity.ModelYear;

public interface ModelYearRepository extends JpaRepository<ModelYear, Integer>, JpaSpecificationExecutor<ModelYear> {

}
