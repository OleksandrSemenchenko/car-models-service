package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.config.PageSortConfig;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@Validated
public class ManufacturerController {
    
    private final ManufacturerService manufacturerService;
    private final PageSortConfig sortConfig;
    
    @GetMapping("/{name}")
    public ManufacturerDto getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping
    public Page<ManufacturerDto> getAll(Pageable pageable) {
        Pageable pageableDef = setDefaults(pageable);
        return manufacturerService.getAll(pageableDef);
    }
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid ManufacturerDto manufacturer) {
        ManufacturerDto createdManufacturer = manufacturerService.save(manufacturer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}")
                                                  .buildAndExpand(createdManufacturer.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
    }
    
    private Pageable setDefaults(Pageable pageable) {
        Direction direction = sortConfig.getManufacturerSortDirection();
        String sortParameter = sortConfig.getManufacturerSortParameter();
        Sort sortDef = Sort.by(direction, sortParameter);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(sortDef));
    }
}
