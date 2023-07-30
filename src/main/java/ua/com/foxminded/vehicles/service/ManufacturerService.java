package ua.com.foxminded.vehicles.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.mapper.ManufacturerMapper;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {
    
    public static final String NO_MANUFACTURER = "The manufacturer \"%s\" doesn't exist";

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;
    
    public boolean existsByName(String name) {
        return manufacturerRepository.existsById(name);
    }

    public ManufacturerDto save(ManufacturerDto manufacturerDto) {
        Manufacturer manufacturer = manufacturerMapper.map(manufacturerDto);
        Manufacturer persistedManufacturer = manufacturerRepository.saveAndFlush(manufacturer);
        return manufacturerMapper.map(persistedManufacturer);
    }

    public Page<ManufacturerDto> getAll(Pageable pageable) {
        return manufacturerRepository.findAll(pageable)
                                     .map(manufacturerMapper::map);
    }

    public void deleteByName(String name) {
        manufacturerRepository.findById(name).orElseThrow(
                () -> new NoSuchElementException(String.format(NO_MANUFACTURER, name)));
        manufacturerRepository.deleteById(name);
    }

    public ManufacturerDto getByName(String name) {
        Manufacturer manufacturer = manufacturerRepository.findById(name).orElseThrow(
                () -> new NoSuchElementException(String.format(NO_MANUFACTURER, name)));
        return manufacturerMapper.map(manufacturer);
    }
}
