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

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.mapper.ModelMapper;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {
    
    public static final String MODEL_NAME = "Q7";
    
    @InjectMocks
    private ModelService modelService;
    
    @Mock
    private ModelRepository modelRepository;
    
    @Mock
    private ModelMapper modelMapper;
    
    private Model model;
    
    @BeforeEach
    void SetUp() {
        model = Model.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldPerformCorrectCalls() {
        when(modelRepository.existsById(anyString())).thenReturn(false);
        when(modelMapper.map(isA(ModelDto.class))).thenReturn(model);
        when(modelRepository.save(isA(Model.class))).thenReturn(model);
        ModelDto modelDto = ModelDto.builder().name(MODEL_NAME).build();
        modelService.save(modelDto);
        
        verify(modelRepository).existsById(anyString());
        verify(modelMapper).map(isA(ModelDto.class));
        verify(modelRepository).save(isA(Model.class));
        verify(modelMapper).map(isA(Model.class));
    }
    
    @Test
    void getAll_ShouldPerformCorrectCalls() {
        List<Model> models = Arrays.asList(model);
        when(modelRepository.findAll(isA(Pageable.class))).thenReturn(new PageImpl<Model>(models));
        Pageable pageable = Pageable.unpaged();
        modelService.getAll(pageable);
        
        verify(modelRepository).findAll(isA(Pageable.class));
        verify(modelMapper).map(isA(Model.class));
    }
    
    @Test
    void deleteByName_ShouldPerformCorrectCalls() {
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        modelService.deleteByName(MODEL_NAME);
        
        verify(modelRepository).findById(anyString());
        verify(modelRepository).deleteById(anyString());
    }

    @Test
    void getByName_ShouldPerformCorrectCalls() {
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        modelService.getByName(MODEL_NAME);
        
        verify(modelRepository).findById(anyString());
        verify(modelMapper).map(isA(Model.class));
    }
}
