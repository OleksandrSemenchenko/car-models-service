package ua.foxminded.cars.repository;

import java.time.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.foxminded.cars.repository.entity.ModelYear;

public interface ModelYearRepository
    extends JpaRepository<ModelYear, Year>, JpaSpecificationExecutor<ModelYear> {}
