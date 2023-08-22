package ua.com.foxminded.cars.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.cars.dto.ManufacturerDto;
import ua.com.foxminded.cars.entity.Manufacturer;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.mapper.ManufacturerMapper;
import ua.com.foxminded.cars.repository.ManufacturerRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {
    
    public static final String NO_MANUFACTURER = "The manufacturer '%s' doesn't exist";
    public static final String MANUFACTURER_ALREADY_EXISTS = "The manufacturer '%s' already exists";

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;
    
    public ManufacturerDto save(ManufacturerDto manufacturerDto) {
        if (manufacturerRepository.existsById(manufacturerDto.getName())) {
            throw new AlreadyExistsException(String.format(MANUFACTURER_ALREADY_EXISTS, manufacturerDto.getName())); 
        }
        
        Manufacturer manufacturer = manufacturerMapper.map(manufacturerDto);
        Manufacturer persistedManufacturer = manufacturerRepository.save(manufacturer);
        return manufacturerMapper.map(persistedManufacturer);
    }

    public Page<ManufacturerDto> getAll(Pageable pageable) {
        return manufacturerRepository.findAll(pageable).map(manufacturerMapper::map);
    }

    public void deleteByName(String name) {
        manufacturerRepository.findById(name).orElseThrow(
                () -> new NotFoundException(String.format(NO_MANUFACTURER, name)));
        manufacturerRepository.deleteById(name);
    }

    public Optional<ManufacturerDto> getByName(String name) {
        return manufacturerRepository.findById(name).map(manufacturerMapper::map);
    }
}
