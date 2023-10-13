package ua.com.foxminded.cars.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ua.com.foxminded.cars.dto.ManufacturerDto;
import ua.com.foxminded.cars.entity.Manufacturer;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.DatabaseConstraintsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.mapper.ManufacturerMapper;
import ua.com.foxminded.cars.mapper.ManufacturerMapperImpl;
import ua.com.foxminded.cars.repository.ManufacturerRepository;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    @InjectMocks
    private ManufacturerService manufacturerService;
    
    @Mock
    private ManufacturerRepository manufacturerRepository;
    
    @Spy
    private ManufacturerMapper manufacturerMapper = new ManufacturerMapperImpl();
    
    private Manufacturer manufacturer;
    private ManufacturerDto manufacturerDto;
    
    @BeforeEach
    void SetUp() {
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
    }
    
    @Test
    void save_ShouldSaveManufacturer() {
        when(manufacturerRepository.existsById(manufacturerDto.getName())).thenReturn(false);
        when(manufacturerMapper.map(manufacturerDto)).thenReturn(manufacturer);
        manufacturerService.save(manufacturerDto);
        
        verify(manufacturerRepository).existsById(manufacturerDto.getName());
        verify(manufacturerMapper).map(manufacturerDto);
        verify(manufacturerRepository).save(manufacturer);
    }
    
    @Test
    void save_ShouldThrow_WhenNoSuchManufacturer() {
        when(manufacturerRepository.existsById(manufacturerDto.getName())).thenReturn(true);
        
        assertThrows(AlreadyExistsException.class, () -> manufacturerService.save(manufacturerDto));
    }
    
    @Test
    void getAll_ShouldGetManufacturers() {
        List<Manufacturer> manufacturers = Arrays.asList(manufacturer);
        Pageable pageable = Pageable.unpaged();
        Page<Manufacturer> manufacturerPage = new PageImpl<>(manufacturers);
        when(manufacturerRepository.findAll(pageable)).thenReturn(manufacturerPage);
        manufacturerService.getAll(pageable);
        
        verify(manufacturerRepository).findAll(pageable);
        verify(manufacturerMapper).map(manufacturer);
    }
    
    @Test
    void deleteByName_ShouldDeleteManufacturer() {
        when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));
        manufacturerService.deleteByName(MANUFACTURER_NAME);
        
        verify(manufacturerRepository).findById(MANUFACTURER_NAME);
        verify(manufacturerRepository).findByModelsManufacturerName(MANUFACTURER_NAME);
        verify(manufacturerRepository).deleteById(MANUFACTURER_NAME);
    }
    
    @Test
    void deleteByName_ShouldThrow_WhenManufacturerHasRelations() {
        when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));
        when(manufacturerRepository.findByModelsManufacturerName(MANUFACTURER_NAME))
                .thenReturn(Optional.of(manufacturer));
        
        assertThrows(DatabaseConstraintsException.class, () -> manufacturerService.deleteByName(MANUFACTURER_NAME));
    }
    
    @Test
    void deleteByName_ShouldThrow_WhenNoSuchManufacturer() {
        when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> manufacturerService.deleteByName(MANUFACTURER_NAME));
    }
    
    @Test
    void getByName_ShouldGetManufacturer() {
        when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));
        manufacturerService.getByName(MANUFACTURER_NAME);
        
        verify(manufacturerRepository).findById(MANUFACTURER_NAME);
        verify(manufacturerMapper).map(manufacturer);
    }
}
