package ua.com.foxminded.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.service.CategoryServiceTest.CATEGORY_NAME;
import static ua.com.foxminded.service.ManufacturerServiceTest.MANUFACTURER_NAME;
import static ua.com.foxminded.service.ModelNameServiceTest.MODEL_NAME;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import ua.com.foxminded.dto.ModelDto;
import ua.com.foxminded.entity.Category;
import ua.com.foxminded.entity.Manufacturer;
import ua.com.foxminded.entity.Model;
import ua.com.foxminded.entity.ModelName;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.ModelMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.ModelNameRepository;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.specification.SearchFilter;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {
    
    public static final String NEW_MODEL_ID = "2";
    public static final String MODEL_ID = "1";
    public static final int YEAR = 2020;
    public static final int NEW_YEAR = 2010;
    
    @InjectMocks
    private ModelService modelService;
    
    @Mock
    private ModelRepository modelRepository;
    
    @Mock 
    private ManufacturerRepository manufacturerRepository;
    
    @Mock
    private ModelNameRepository modelNameRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Spy
    private ModelMapper modelMapper;
    
    private Manufacturer manufacturer;
    private ModelName modelName;
    private Category category;
    private Model model;
    private ModelDto modelDto;
    
    @BeforeEach
    void SetUp() {
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        modelName = ModelName.builder().name(MODEL_NAME).build();
        category = Category.builder().name(CATEGORY_NAME).build();
        model = Model.builder().id(MODEL_ID)
                               .year(YEAR)
                               .manufacturer(manufacturer)
                               .modelName(modelName)
                               .categories(new HashSet<>(Arrays.asList(category)))
                               .build();
        category.setModels(new HashSet<>(Arrays.asList(model)));
        
        modelDto = ModelDto.builder().id(MODEL_ID)
                                     .year(YEAR)
                                     .manufacturer(MANUFACTURER_NAME)
                                     .name(MODEL_NAME)
                                     .categories(new HashSet<String>(Arrays.asList(CATEGORY_NAME)))
                                     .build();
    }
    
    @Test
    void getByManufacturerAndNameAndYear_ShouldThrowNotFoundException_WhenNoSuchModel() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.getByManufacturerAndNameAndYear(
                MANUFACTURER_NAME, MODEL_NAME, NEW_YEAR));
    }
    
    @Test
    void getByManufacturerAndNameAndYear_ShouldReturnModel() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.of(model));
        modelService.getByManufacturerAndNameAndYear(MANUFACTURER_NAME, MODEL_NAME, YEAR);
        
        verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
        verify(modelMapper).map(isA(Model.class));
    }
    
    @Test
    void search_ShouldSeachModels() {
        SearchFilter seachFilter = new SearchFilter();
        Pageable pageable = Pageable.unpaged();
        Page<Model> modelsPage = new PageImpl<Model>(Arrays.asList(model));
        when(modelRepository.findAll(ArgumentMatchers.<Specification<Model>>any(), isA(Pageable.class)))
                .thenReturn(modelsPage);
        modelService.search(seachFilter, pageable);
        
        verify(modelRepository).findAll(ArgumentMatchers.<Specification<Model>>any(), isA(Pageable.class));
        verify(modelMapper).map(model);
    }
    
    @Test
    void save_ShouldSaveModel() {
        model.setId(null);
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        when(categoryRepository.findById(modelDto.getCategories()
                                                 .iterator()
                                                 .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(modelDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelNameRepository.findById(modelDto.getName())).thenReturn(Optional.of(modelName));
        when(modelRepository.save(model)).thenReturn(model);
        modelService.save(modelDto);

        verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
        verify(categoryRepository).findById(modelDto.getCategories().iterator().next());
        verify(modelNameRepository).findById(modelDto.getName());
        verify(modelRepository).save(model);
        verify(modelMapper).map(model);
    }
    
    @Test
    void save_ShouldThrowNotFoundException_WhenNoSuchModelName() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        when(categoryRepository.findById(modelDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(modelDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelNameRepository.findById(modelDto.getName())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> modelService.save(modelDto));
    }
    
    @Test
    void save_ShouldThrowNotFoundException_WhenNoSuchManufacturer() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        when(categoryRepository.findById(modelDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(modelDto.getManufacturer())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> modelService.save(modelDto));
    }
    
    @Test
    void save_ShouldThrowNotFoundException_WhenNoSuchCategory() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        when(categoryRepository.findById(modelDto.getCategories().iterator().next())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.save(modelDto));
    }
    
    @Test
    void save_ShouldThrowAlreadyExistsException_WhenModelAlreadyExists() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.of(model));
        
        assertThrows(AlreadyExistsException.class, () -> modelService.save(modelDto));
    }
    
    @Test
    void update_ShouldThrowNotFoundException_WhenNoRequiredCategory() {
        String updatedCategoryName = "Pickup";
        modelDto.setCategories(Set.of(updatedCategoryName));
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(modelDto.getCategories().iterator().next())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> modelService.update(modelDto));
    }
    
    @Test
    void update_ShouldThrowNotFoundException_WhenNoSuchModel() {
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.update(modelDto));
    }
    
    @Test
    void update_ShouldUpdateModel() {
        String updatedCategoryName = "Pickup";
        Category updatedCategory = Category.builder().name(updatedCategoryName)
                                                     .models(new HashSet<>())
                                                     .build();
        modelDto.setCategories(Set.of(updatedCategoryName));
        when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(modelDto.getCategories()
                                                 .iterator()
                                                 .next())).thenReturn(Optional.of(updatedCategory));
        Model updatedVehicle = Model.builder().id(model.getId())
                                              .year(modelDto.getYear())
                                              .manufacturer(manufacturer)
                                              .modelName(modelName)
                                              .categories(new HashSet<Category>(Arrays.asList(updatedCategory)))
                                              .build();
        when(modelRepository.save(updatedVehicle)).thenReturn(updatedVehicle);
        modelService.update(modelDto);
        
        verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
        verify(categoryRepository).findById(modelDto.getCategories().iterator().next());
        verify(modelRepository).save(updatedVehicle);
        verify(modelMapper).map(updatedVehicle);
    }
    
    @Test
    void deleteById_ShouldThrowNotFoundException_WhenNoModel() {
        when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.deleteById(MODEL_ID));
    }
    
    @Test
    void deleteById_ShouldDeleteModel() {
        when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
        modelService.deleteById(MODEL_ID);
        
        verify(modelRepository).findById(MODEL_ID);
        verify(modelRepository).deleteById(MODEL_ID);
    }
    
    @Test
    void getById_ShouldThrowNotFoundException_WhenNoModelWithSuchId() {
        when(modelRepository.findById(NEW_MODEL_ID)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> modelService.getById(NEW_MODEL_ID));
    }

    @Test
    void getById_ShouldReturnModel() {
        when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
        modelService.getById(MODEL_ID);
        
        verify(modelRepository).findById(MODEL_ID);
        verify(modelMapper).map(model);
    }
}
