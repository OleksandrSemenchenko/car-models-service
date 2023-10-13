package ua.com.foxminded.cars.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.com.foxminded.cars.entity.Manufacturer;

@DataJpaTest
class ManufacturerRepositoryTest {
    
    public static final String MANUFACTURER = "Audi";
    
    @Autowired
    ManufacturerRepository manufacturerRepsotory;

    @Test
    void findByManufacturerModel_ShouldReturnManufacturer() {
        Manufacturer manufacturer = manufacturerRepsotory.findByModelsManufacturerName(MANUFACTURER);
        
        assertEquals(MANUFACTURER, manufacturer.getName());
    }
}
