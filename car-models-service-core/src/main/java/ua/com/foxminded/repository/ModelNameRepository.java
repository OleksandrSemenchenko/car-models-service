package ua.com.foxminded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.entity.ModelName;

public interface ModelNameRepository extends JpaRepository<ModelName, String> {
    
}
