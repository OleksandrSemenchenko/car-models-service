package ua.com.foxminded.vehicles.service;

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

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.mapper.ModelMapper;
import ua.com.foxminded.vehicles.mapper.ModelMapperImpl;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {
    
    public static final String MODEL_NAME = "Q7";
    
    @InjectMocks
    private ModelService modelService;
    
    @Mock
    private ModelRepository modelRepository;
    
    @Spy
    private ModelMapper modelMapper = new ModelMapperImpl();
    
    private Model model;
    private ModelDto modelDto;
    
    @BeforeEach
    void SetUp() {
        model = Model.builder().name(MODEL_NAME).build();
        modelDto = ModelDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldSaveModel() {
        when(modelRepository.existsById(modelDto.getName())).thenReturn(false);
        when(modelRepository.save(model)).thenReturn(model);
        modelService.save(modelDto);
        
        verify(modelRepository).existsById(modelDto.getName());
        verify(modelMapper).map(modelDto);
        verify(modelRepository).save(model);
        verify(modelMapper).map(model);
    }
    
    @Test
    void save_ShouldThrow_WhenNoSuchModel() {
        when(modelRepository.existsById(modelDto.getName())).thenReturn(true);
        
        assertThrows(AlreadyExistsException.class, () -> modelService.save(modelDto));
    }
    
    @Test
    void getAll_ShouldGetModels() {
        List<Model> models = Arrays.asList(model);
        Page<Model> modelsPage = new PageImpl<>(models);
        Pageable pageable = Pageable.unpaged();
        when(modelRepository.findAll(pageable)).thenReturn(modelsPage);
        modelService.getAll(pageable);
        
        verify(modelRepository).findAll(pageable);
        verify(modelMapper).map(model);
    }
    
    @Test
    void deleteByName_ShouldDeleteModel() {
        when(modelRepository.findById(MODEL_NAME)).thenReturn(Optional.of(model));
        modelService.deleteByName(MODEL_NAME);
        
        verify(modelRepository).findById(MODEL_NAME);
        verify(modelRepository).deleteById(MODEL_NAME);
    }
    
    @Test
    void deleteByName_ShouldThrow_WhenNoSuchModel() {
        when(modelRepository.findById(MODEL_NAME)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.deleteByName(MODEL_NAME));
    }

    @Test
    void getByName_ShouldGetModel() {
        when(modelRepository.findById(MODEL_NAME)).thenReturn(Optional.of(model));
        modelService.getByName(MODEL_NAME);
        
        verify(modelRepository).findById(MODEL_NAME);
        verify(modelMapper).map(model);
    }
}
