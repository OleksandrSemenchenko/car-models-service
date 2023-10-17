package ua.com.foxminded.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import ua.com.foxminded.dto.ModelNameDto;
import ua.com.foxminded.entity.Model;
import ua.com.foxminded.entity.ModelName;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.DatabaseConstraintException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.ModelNameMapper;
import ua.com.foxminded.mapper.ModelNameMapperImpl;
import ua.com.foxminded.repository.ModelNameRepository;

@ExtendWith(MockitoExtension.class)
class ModelNameServiceTest {
    
    public static final String NEW_MODEL_NAME = "Edge";
    public static final String MODEL_NAME = "A7";
    public static final String MODEL_ID = "1";
    
    @InjectMocks
    private ModelNameService modelNameService;
    
    @Mock
    private ModelNameRepository modelNameRepository;
    
    @Spy
    private ModelNameMapper modelNameMapper = new ModelNameMapperImpl();
    
    private ModelName modelName;
    private ModelNameDto modelNameDto;
    
    @BeforeEach
    void SetUp() {
        modelName = ModelName.builder().name(MODEL_NAME).build();
        modelNameDto = ModelNameDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldSaveModelName() {
        when(modelNameRepository.existsById(modelNameDto.getName())).thenReturn(false);
        when(modelNameRepository.save(modelName)).thenReturn(modelName);
        modelNameService.save(modelNameDto);
        
        verify(modelNameRepository).existsById(modelNameDto.getName());
        verify(modelNameMapper).map(modelNameDto);
        verify(modelNameRepository).save(modelName);
        verify(modelNameMapper).map(modelName);
    }
    
    @Test
    void save_ShouldThrowAlreadyExistsException_WhenNoSuchModelName() {
        when(modelNameRepository.existsById(modelNameDto.getName())).thenReturn(true);
        
        assertThrows(AlreadyExistsException.class, () -> modelNameService.save(modelNameDto));
    }
    
    @Test
    void getAll_ShouldGetModelNames() {
        List<ModelName> modelNames = Arrays.asList(modelName);
        Page<ModelName> modelNamesPage = new PageImpl<>(modelNames);
        Pageable pageable = Pageable.unpaged();
        when(modelNameRepository.findAll(pageable)).thenReturn(modelNamesPage);
        modelNameService.getAll(pageable);
        
        verify(modelNameRepository).findAll(pageable);
        verify(modelNameMapper).map(modelName);
    }
    
    @Test
    void deleteByName_ShouldDeleteModelName() {
        modelName.setModels(new HashSet<>());
        when(modelNameRepository.findById(MODEL_NAME)).thenReturn(Optional.of(modelName));
        modelNameService.deleteByName(MODEL_NAME);
        
        verify(modelNameRepository).findById(MODEL_NAME);
        verify(modelNameRepository).deleteById(MODEL_NAME);
    }
    
    @Test
    void deleteByName_ShouldThrowDatabaseConstraintException_WhenModelNameHasRelations() {
        Model model = Model.builder().id(MODEL_ID).build();
        modelName.setModels(Set.of(model));
        when(modelNameRepository.findById(MODEL_NAME)).thenReturn(Optional.of(modelName));
        
        assertThrows(DatabaseConstraintException.class, () -> modelNameService.deleteByName(MODEL_NAME));
    }
    
    @Test
    void deleteByName_ShouldThrowNotFoundException_WhenNoSuchModelName() {
        when(modelNameRepository.findById(NEW_MODEL_NAME)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelNameService.deleteByName(NEW_MODEL_NAME));
    }
    
    @Test
    void getByName_ShouldThrowNotFoundException_WhenNoSuchModelName() {
        when(modelNameRepository.findById(NEW_MODEL_NAME)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelNameService.getByName(NEW_MODEL_NAME));
    }
    
    @Test
    void getByName_ShouldGetModelName() {
        when(modelNameRepository.findById(MODEL_NAME)).thenReturn(Optional.of(modelName));
        modelNameService.getByName(MODEL_NAME);
        
        verify(modelNameRepository).findById(MODEL_NAME);
        verify(modelNameMapper).map(modelName);
    }
}
