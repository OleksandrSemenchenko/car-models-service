package ua.com.foxminded.vehicles.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.mapper.ModelMapper;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {

    public static final String NO_MODEL = "The model \"%s\" doesn't exist";

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    
    public boolean existsByName(String name) {
        return modelRepository.existsById(name);
    }

    public ModelDto save(ModelDto modelDto) {
        Model model = modelMapper.map(modelDto);
        Model persistedModel = modelRepository.saveAndFlush(model);
        return modelMapper.map(persistedModel);
    }

    public Page<ModelDto> getAll(Pageable pageable) {
        return modelRepository.findAll(pageable)
                              .map(modelMapper::map);
    }

    public void deleteByName(String name) {
        modelRepository.findById(name)
                       .orElseThrow(() -> new NoSuchElementException(String.format(NO_MODEL, name)));
        modelRepository.deleteById(name);
    }

    public ModelDto getByName(String name) {
        Model model = modelRepository.findById(name).orElseThrow(
                () -> new NoSuchElementException(String.format(NO_MODEL, name)));
        return modelMapper.map(model);
    }
}
