package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ua.com.foxminded.vehicles.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String>{
    
    @Modifying
    @Query("update Category c set c.name = ?1 where c.name = ?2")
    public void updateName(String newName,String oldName);
}
