package ua.com.foxminded.vehicles.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    
    public static final String NO_MANUFACTURER = "The manufacturer '%s' doesn't exist";
    public static final String MANUFACTURER_IS_PRESENT = "The manufacturer '%s' already exists";

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;
    
    public ManufacturerDto save(ManufacturerDto manufacturerDto) {
        if (manufacturerRepository.existsById(manufacturerDto.getName())) {
            throw new ServiceException(String.format(MANUFACTURER_IS_PRESENT, manufacturerDto.getName()), 
                                       HttpStatus.CONFLICT); 
        }
        
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
                () -> new ServiceException(String.format(NO_MANUFACTURER, name), HttpStatus.NOT_FOUND));
        manufacturerRepository.deleteById(name);
    }

    public ManufacturerDto getByName(String name) {
        Manufacturer manufacturer = manufacturerRepository.findById(name).orElseThrow(
                () -> new ServiceException(String.format(NO_MANUFACTURER, name), HttpStatus.NOT_FOUND));
        return manufacturerMapper.map(manufacturer);
    }
}
