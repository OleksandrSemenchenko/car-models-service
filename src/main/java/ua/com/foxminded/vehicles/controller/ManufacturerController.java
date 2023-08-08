package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
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
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@Validated
public class ManufacturerController {
    
    @Value("${application.sort.manufacturer.by}")
    private String sortBy;
    
    @Value("${application.sort.manufacturer.direction}")
    private String sortDirection;
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public ResponseEntity<ManufacturerDto> getByName(@PathVariable String name) {
        ManufacturerDto manufacturer = manufacturerService.getByName(name);
        return ResponseEntity.ok(manufacturer);
    }
    
    @GetMapping
    public Page<ManufacturerDto> getAll(Pageable pageable) {
        Pageable pageableDefault = setDefaults(pageable);
        return manufacturerService.getAll(pageableDefault);
    }
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid ManufacturerDto manufacturer) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}")
                                                  .buildAndExpand(manufacturer.getName())
                                                  .toUri();
        
        if (manufacturerService.existsByName(manufacturer.getName())) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                                 .location(location).build();
        } else {
            manufacturerService.save(manufacturer);
            return ResponseEntity.created(location).build();
        }
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaults(Pageable pageable) {
        Direction direction = Direction.valueOf(sortDirection);
        Sort sortDefault = Sort.by(direction, sortBy);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(sortDefault));
    }
}
