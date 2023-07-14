package ua.com.foxminded.vehicles.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import ua.com.foxminded.vehicles.entity.ManufacturerEntity;
import ua.com.foxminded.vehicles.entitymother.ManufacturerEntityMother;


@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ManufacturerRepositoryTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    private ManufacturerEntity manufacturer;
    
    @BeforeEach
    void setUp() {
        manufacturer = ManufacturerEntityMother.complete().build();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        
        entityManager.persist(manufacturer);
        entityManager.getTransaction().commit();
    }
    
    @Test
    void updateName_ShouldUpdateName() {
        String newName = "Volkswagen";
        manufacturerRepository.updateName(newName, manufacturer.getName());
        Optional<ManufacturerEntity> updatedManufacturerOpt = manufacturerRepository.findById(newName);
        assertTrue(updatedManufacturerOpt.isPresent());
    }
}
