package ua.com.foxminded.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.com.foxminded.vehicles.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    
    @Modifying
    @Query("update Manufacturer m set m.name = :newName where m.name = :oldName")
    public void updateName(@Param("newName") String newName, @Param("oldName") String oldName);
}
