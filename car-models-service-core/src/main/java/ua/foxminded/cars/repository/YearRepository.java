package ua.foxminded.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.foxminded.cars.repository.entity.Year;

public interface YearRepository
    extends JpaRepository<Year, Integer>, JpaSpecificationExecutor<Year> {}
