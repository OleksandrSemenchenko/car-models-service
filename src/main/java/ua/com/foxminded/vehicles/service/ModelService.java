package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.exception.ErrorCode.MODEL_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_ABSENCE;

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

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelDto save(ModelDto model) {
        Model entity = modelMapper.map(model);
        Model persistedEntity = modelRepository.saveAndFlush(entity);
        return modelMapper.map(persistedEntity);
    }

    public Page<ModelDto> getAll(Pageable pageable) {
        return modelRepository.findAll(pageable).map(modelMapper::map);
    }

    public void deleteByName(String name) {
            modelRepository.findById(name).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.deleteById(name);
    }

    public ModelDto getByName(String name) {
            Model entity = modelRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            return modelMapper.map(entity);
    }
}
