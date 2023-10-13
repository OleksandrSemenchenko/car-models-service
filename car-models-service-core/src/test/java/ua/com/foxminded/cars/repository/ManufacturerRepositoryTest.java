package ua.com.foxminded.cars.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.com.foxminded.cars.entity.Manufacturer;

@DataJpaTest
class ManufacturerRepositoryTest {
    
    public static final String MANUFACTURER = "Audi";
    
    @Autowired
    private ManufacturerRepository manufacturerRepsotory;

    @Test
    void findByManufacturerModel_ShouldReturnManufacturer() {
        Optional<Manufacturer> manufacturerOptional = manufacturerRepsotory.findByModelsManufacturerName(MANUFACTURER);
        
        assertEquals(MANUFACTURER, manufacturerOptional.get().getName());
    }
}
