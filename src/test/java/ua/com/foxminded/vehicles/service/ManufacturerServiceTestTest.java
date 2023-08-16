package ua.com.foxminded.vehicles.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.mapper.ManufacturerMapper;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTestTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    @InjectMocks
    private ManufacturerService manufacturerService;
    
    @Mock
    private ManufacturerRepository manufacturerRepository;
    
    @Mock
    private ManufacturerMapper manufacturerMapper;
    
    private Manufacturer manufacturer;
    
    @BeforeEach
    void SetUp() {
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
    }
    
    @Test
    void save_ShouldPerformCorrectCalls() {
        when(manufacturerRepository.existsById(anyString())).thenReturn(false);
        when(manufacturerMapper.map(isA(ManufacturerDto.class))).thenReturn(manufacturer);
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
        manufacturerService.save(manufacturerDto);
        
        verify(manufacturerRepository).existsById(anyString());
        verify(manufacturerMapper).map(isA(ManufacturerDto.class));
        verify(manufacturerRepository).save(isA(Manufacturer.class));
    }
    
    @Test
    void getAll_ShouldPerformCorrectCalls() {
        List<Manufacturer> manufacturers = Arrays.asList(manufacturer);
        when(manufacturerRepository.findAll(isA(Pageable.class))).thenReturn(new PageImpl<Manufacturer>(manufacturers));
        Pageable pageable = Pageable.unpaged();
        manufacturerService.getAll(pageable);
        
        verify(manufacturerRepository).findAll(isA(Pageable.class));
        verify(manufacturerMapper).map(isA(Manufacturer.class));
    }
    
    @Test
    void deleteByName_ShouldPerformCorrectCalls() {
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        manufacturerService.deleteByName(MANUFACTURER_NAME);
        
        verify(manufacturerRepository).findById(anyString());
        verify(manufacturerRepository).deleteById(anyString());
    }
    
    @Test
    void getByName_ShouldPerformCorrectCalls() {
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        manufacturerService.getByName(MANUFACTURER_NAME);
        
        verify(manufacturerRepository).findById(anyString());
        verify(manufacturerMapper).map(isA(Manufacturer.class));
    }
}
