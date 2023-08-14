package ua.com.foxminded.vehicles.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.mapper.ModelMapper;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {

    public static final String NO_MODEL = "The model '%s' doesn't exist";
    public static final String MODEL_ALREADY_EXISTS = "The model '%s' already exists";

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    
    public ModelDto save(ModelDto modelDto) {
        if (modelRepository.existsById(modelDto.getName())) {
            throw new AlreadyExistsException(String.format(MODEL_ALREADY_EXISTS, modelDto.getName()));
        }
        
        Model model = modelMapper.map(modelDto);
        Model persistedModel = modelRepository.saveAndFlush(model);
        return modelMapper.map(persistedModel);
    }

    public Page<ModelDto> getAll(Pageable pageable) {
        return modelRepository.findAll(pageable)
                              .map(modelMapper::map);
    }

    public void deleteByName(String name) {
        modelRepository.findById(name).orElseThrow(
                () -> new NotFoundException(String.format(NO_MODEL, name)));
        modelRepository.deleteById(name);
    }

    public ModelDto getByName(String name) {
        Model model = modelRepository.findById(name).orElseThrow(
                () -> new NotFoundException(String.format(NO_MODEL, name)));
        return modelMapper.map(model);
    }
}
