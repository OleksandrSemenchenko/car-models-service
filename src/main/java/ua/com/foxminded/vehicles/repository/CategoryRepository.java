package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ua.com.foxminded.vehicles.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String>{
    
    @Modifying
    @Query("update CategoryEntity c set c.name = ?1 where c.name = ?2")
    public void updateName(String newName,String oldName);
}
