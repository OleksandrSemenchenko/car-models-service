package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_CREATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_DELETE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_FETCH_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_UPDATE_ERROR;

import java.lang.reflect.Type;

import org.hibernate.boot.MappingException;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {
    
    public static final Type MANUFACTURERS_PAGE_TYPE = new TypeToken<Page<ManufacturerDto>>() {}.getType();
    
    private final ModelMapper modelMapper;
    private final ManufacturerRepository manufacturerRepository;
    
    public ManufacturerDto save(ManufacturerDto model) {
        try {
            Manufacturer entity = modelMapper.map(model, Manufacturer.class);
            Manufacturer persistedEntity = manufacturerRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, ManufacturerDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_CREATE_ERROR, e);
        }
    }
    
    public Page<ManufacturerDto> getAll(Pageable pageable) {
        try {
            Page<Manufacturer> entities = manufacturerRepository.findAll(pageable);
            return modelMapper.map(entities, MANUFACTURERS_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_FETCH_ERROR, e);
        }
    }
    
    public ManufacturerDto updateName(String newName, String oldName) {
        try {
            manufacturerRepository.findById(oldName)
                .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            manufacturerRepository.updateName(newName, oldName);
            Manufacturer updatedEntity = manufacturerRepository.findById(newName).orElseThrow();
            return modelMapper.map(updatedEntity, ManufacturerDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_UPDATE_ERROR, e);
        }
    }
    
    public void deleteByName(String name) {
        try {
            manufacturerRepository.findById(name)
                .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            manufacturerRepository.deleteById(name);
        } catch (DataAccessException e) {
            throw new ServiceException(MANUFACTURER_DELETE_ERROR);
        }
    }
    
    public ManufacturerDto getByName(String name) {
        try {
            Manufacturer entity = manufacturerRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            return modelMapper.map(entity, ManufacturerDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_FETCH_ERROR, e);
        }
    }
}
