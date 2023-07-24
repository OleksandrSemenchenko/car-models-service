package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_ABSENCE;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.mapper.ManufacturerMapper;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public ManufacturerDto save(ManufacturerDto model) {
        Manufacturer entity = manufacturerMapper.map(model);
        Manufacturer persistedEntity = manufacturerRepository.saveAndFlush(entity);
        return manufacturerMapper.map(persistedEntity);
    }

    public Page<ManufacturerDto> getAll(Pageable pageable) {
        return manufacturerRepository.findAll(pageable).map(manufacturerMapper::map);
    }

    public void deleteByName(String name) {
        manufacturerRepository.findById(name).orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
        manufacturerRepository.deleteById(name);
    }

    public ManufacturerDto getByName(String name) {
        Manufacturer entity = manufacturerRepository.findById(name)
                .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
        return manufacturerMapper.map(entity);
    }
}
