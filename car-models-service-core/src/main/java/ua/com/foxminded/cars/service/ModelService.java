package ua.com.foxminded.cars.service;

import static ua.com.foxminded.cars.service.CategoryService.NO_CATEGORY;
import static ua.com.foxminded.cars.service.ManufacturerService.NO_MANUFACTURER;
import static ua.com.foxminded.cars.service.ModelNameService.NO_MODEL_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.cars.dto.CategoryDto;
import ua.com.foxminded.cars.dto.ModelDto;
import ua.com.foxminded.cars.entity.Category;
import ua.com.foxminded.cars.entity.Model;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.mapper.ModelMapper;
import ua.com.foxminded.cars.repository.CategoryRepository;
import ua.com.foxminded.cars.repository.ManufacturerRepository;
import ua.com.foxminded.cars.repository.ModelNameRepository;
import ua.com.foxminded.cars.repository.ModelRepository;
import ua.com.foxminded.cars.specification.ModelSpecification;
import ua.com.foxminded.cars.specification.SearchFilter;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {

    public static final String NO_SUCH_MODEL = "Such model doesn't exist";
    public static final String NO_MODEL_ID = "The model with id=%s doesn't exist";
    public static final String MODEL_ALREADY_EXISTS = "Such model with id='%s' already exists";

    private final ModelRepository modelRepository;
    private final CategoryRepository categoryRepository;
    private final ModelNameRepository modelNameRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ModelMapper modelMapper;
    
    public Optional<ModelDto> getByManufacturerAndNameAndYear(String manufacturer, String name, int year) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(manufacturer).model(name).year(year).build();
        Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
        return modelRepository.findOne(specification).map(modelMapper::map);
    }
    
    public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageRequest) {
        Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
        return modelRepository.findAll(specification, pageRequest).map(modelMapper::map);
    }

    public ModelDto save(ModelDto modelDto) {
        throwIfPresentByManufacturerAndModelAndYear(modelDto.getManufacturer(), 
                                                    modelDto.getName(), 
                                                    modelDto.getYear()); 
        
        var model = Model.builder().year(modelDto.getYear())
                                   .categories(new HashSet<>())
                                   .build();
        
        updateCategoryRelations(modelDto, model);
        updateManufacturerRelation(modelDto, model);
        updateModelRelation(modelDto, model);

        var persistedVehicle = modelRepository.save(model);
        return modelMapper.map(persistedVehicle);
    }

    public ModelDto update(ModelDto modelDto) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(modelDto.getManufacturer())
                                                          .model(modelDto.getName())
                                                          .year(modelDto.getYear())
                                                          .build();
        Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
        var model = modelRepository.findOne(specification).orElseThrow(() -> new NotFoundException(NO_SUCH_MODEL));
        model.setYear(modelDto.getYear());
        
        updateManufacturerRelation(modelDto, model);
        updateModelRelation(modelDto, model);
        updateCategoryRelations(modelDto, model);

        var updatedVehicle = modelRepository.save(model);
        return modelMapper.map(updatedVehicle);
    }

    public void deleteById(String id) {
        modelRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_ID, id)));
        modelRepository.deleteById(id);
    }

    public Optional<ModelDto> getById(String id) {
        return modelRepository.findById(id).map(modelMapper::map);
    }
    
    private void throwIfPresentByManufacturerAndModelAndYear(String manufacturer, String model, int year) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(manufacturer)
                                                          .model(model)
                                                          .year(year).build();
        Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
        Optional<Model> vehicleOptional = modelRepository.findOne(specification);
        
        if (vehicleOptional.isPresent()) {
            throw new AlreadyExistsException(String.format(MODEL_ALREADY_EXISTS, vehicleOptional.get().getId())); 
        }
    }

    private void updateCategoryRelations(ModelDto modelDto, Model model) {
        List<Category> unnecessaryCategories = model.getCategories().stream().filter(category -> {
            return modelDto.getCategories().stream().noneMatch(categoryName -> category.getName().equals(categoryName));
        }).toList();

        for (Category category : unnecessaryCategories) {
            model.removeCategory(category);
        }

        Set<CategoryDto> necessaryCategories = modelDto.getCategories().stream().filter(categoryDto -> {
            return model.getCategories().stream().noneMatch(category -> categoryDto.equals(category.getName()));
        }).map(categoryName -> CategoryDto.builder().name(categoryName).build())
          .collect(Collectors.toSet());

        addCategoryRelations(necessaryCategories, model);
    }
    
    private void addCategoryRelations(Set<CategoryDto> categoriesDto, Model model) {
        for (CategoryDto categoryDto : categoriesDto) {
            var categoryName = categoryDto.getName();
            var category = categoryRepository.findById(categoryName).orElseThrow(
                    () -> new NotFoundException(String.format(NO_CATEGORY, categoryName)));
            model.addCategory(category);
        }
    }

    private void updateModelRelation(ModelDto modelDto, Model model) {
        if (modelDto.getName() != null) {
            var name = modelDto.getName();
            var modelName = modelNameRepository.findById(name).orElseThrow(
                    () -> new NotFoundException(String.format(NO_MODEL_NAME, name)));
            model.setModelName(modelName);
        } else {
            model.setModelName(null);
        }
    }

    private void updateManufacturerRelation(ModelDto modelDto, Model model) {
        if (modelDto.getManufacturer() != null) {
            var manufacturerName = modelDto.getManufacturer();
            var manufacturer = manufacturerRepository.findById(manufacturerName).orElseThrow(
                    () -> new NotFoundException(String.format(NO_MANUFACTURER, manufacturerName)));
            model.setManufacturer(manufacturer);
        } else {
            model.setManufacturer(null);
        }
    }
}
