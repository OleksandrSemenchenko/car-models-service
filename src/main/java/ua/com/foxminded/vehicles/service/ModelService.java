package ua.com.foxminded.vehicles.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.mapper.ModelMapper;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {
    
    public static final String MODEL_IS_NOT_PRESENT = "The model \"%s\" doesn't exist"; 
    public static final String MODEL_IS_PRESENT = "The model \"%s\" already exists";

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelDto save(ModelDto model) {
        if (modelRepository.findById(model.getName()).isPresent()) {
            throw new ServiceException(String.format(MODEL_IS_PRESENT, model.getName()), BAD_REQUEST);
        }
        
        Model entity = modelMapper.map(model);
        Model persistedEntity = modelRepository.saveAndFlush(entity);
        return modelMapper.map(persistedEntity);
    }

    public Page<ModelDto> getAll(Pageable pageable) {
        return modelRepository.findAll(pageable).map(modelMapper::map);
    }

    public void deleteByName(String name) {
            modelRepository.findById(name).orElseThrow(
                    () -> new ServiceException(String.format(MODEL_IS_PRESENT, name), NO_CONTENT));
            modelRepository.deleteById(name);
    }

    public ModelDto getByName(String name) {
            Model entity = modelRepository.findById(name).orElseThrow(
                    () -> new ServiceException(String.format(MODEL_IS_NOT_PRESENT, name), BAD_REQUEST));
            return modelMapper.map(entity);
    }
}
