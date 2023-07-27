package ua.com.foxminded.vehicles.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
    
    public static final String MANUFACTURER_IS_PRESENT = "The manufacturer \"%s\" already exists";
    public static final String MANUFACTURER_IS_NOT_PRESENT = "The manufacturer \"%s\" doesn't exist";

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public ManufacturerDto save(ManufacturerDto manufacturer) {
        if (manufacturerRepository.findById(manufacturer.getName()).isPresent()) {
            throw new ServiceException(String.format(MANUFACTURER_IS_PRESENT, manufacturer.getName()), 
                                                     BAD_REQUEST);
        }
        
        Manufacturer entity = manufacturerMapper.map(manufacturer);
        Manufacturer persistedEntity = manufacturerRepository.saveAndFlush(entity);
        return manufacturerMapper.map(persistedEntity);
    }

    public Page<ManufacturerDto> getAll(Pageable pageable) {
        return manufacturerRepository.findAll(pageable).map(manufacturerMapper::map);
    }

    public void deleteByName(String name) {
        manufacturerRepository.findById(name).orElseThrow(
                () -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, name), NO_CONTENT));
        manufacturerRepository.deleteById(name);
    }

    public ManufacturerDto getByName(String name) {
        Manufacturer entity = manufacturerRepository.findById(name).orElseThrow(
                () -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, name), BAD_REQUEST));
        return manufacturerMapper.map(entity);
    }
}
