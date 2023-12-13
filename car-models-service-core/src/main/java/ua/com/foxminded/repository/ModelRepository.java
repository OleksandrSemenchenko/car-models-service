package ua.com.foxminded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.com.foxminded.entity.Model;

public interface ModelRepository extends JpaRepository<Model, String>, JpaSpecificationExecutor<Model> {
    
}
