package ua.com.foxminded.cars.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.com.foxminded.cars.entity.Manufacturer;

@DataJpaTest
class ManufacturerRepositoryTest {
    
    public static final String MANUFACTURER_NAME_WITHOUT_RELATIONS = "Ford";
    
    @Autowired
    private ManufacturerRepository manufacturerRepsotory;
    
    @Test
    void findByManufacturerModel_ShouldReturnManufacturer() {
        Optional<Manufacturer> manufacturerOptional = manufacturerRepsotory.findByModelsManufacturerName(
                MANUFACTURER_NAME_WITHOUT_RELATIONS);
        
        assertTrue(manufacturerOptional.isEmpty());
    }
}
