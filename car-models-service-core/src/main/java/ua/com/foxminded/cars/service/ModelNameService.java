package ua.com.foxminded.cars.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.cars.dto.ModelNameDto;
import ua.com.foxminded.cars.entity.ModelName;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.DatabaseConstraintsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.mapper.ModelNameMapper;
import ua.com.foxminded.cars.repository.ModelNameRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelNameService {
    
    public static final String MODEL_NAME_DATABASE_CONSTRAINT = 
            "The model name '%s' has relations to models and cannot be removed";
    public static final String NO_MODEL_NAME = "The model name '%s' doesn't exist";
    public static final String MODEL_NAME_ALREADY_EXISTS = "The model name '%s' already exists";

    private final ModelNameRepository modelNameRepository;
    private final ModelNameMapper modeNamelMapper;
    
    public ModelNameDto save(ModelNameDto modelNameDto) {
        if (modelNameRepository.existsById(modelNameDto.getName())) {
            throw new AlreadyExistsException(String.format(MODEL_NAME_ALREADY_EXISTS, modelNameDto.getName()));
        }
        
        ModelName modelName = modeNamelMapper.map(modelNameDto);
        ModelName persistedModelName = modelNameRepository.save(modelName);
        return modeNamelMapper.map(persistedModelName);
    }

    public Page<ModelNameDto> getAll(Pageable pageable) {
        return modelNameRepository.findAll(pageable).map(modeNamelMapper::map);
    }

    public void deleteByName(String name) {
        modelNameRepository.findById(name).orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_NAME, name)));
        modelNameRepository.findByModelsModelNameName(name).ifPresent(modelName -> {
            throw new DatabaseConstraintsException(String.format(MODEL_NAME_DATABASE_CONSTRAINT, name));
        });
        modelNameRepository.deleteById(name);
    }

    public Optional<ModelNameDto> getByName(String name) {
        return modelNameRepository.findById(name).map(modeNamelMapper::map);
    }
}
