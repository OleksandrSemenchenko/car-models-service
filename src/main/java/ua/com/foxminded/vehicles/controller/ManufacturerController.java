package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
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
public class ManufacturerController {
    
    @Value("${application.sort.manufacturer.by}")
    private String manufacturerSortBy;
    
    @Value("${application.sort.manufacturer.direction}")
    private String manufacturerSortDirection;
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public ResponseEntity<ManufacturerDto> getByName(@PathVariable String name) {
        ManufacturerDto manufacturer = manufacturerService.getByName(name);
        return ResponseEntity.ok(manufacturer);
    }
    
    @GetMapping
    public Page<ManufacturerDto> getAll(Pageable pageable) {
        Pageable defaultPageable = setDefaultsForManufacturerPageable(pageable);
        return manufacturerService.getAll(defaultPageable);
    }
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid ManufacturerDto manufacturer) {
        manufacturerService.save(manufacturer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(manufacturer.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaultsForManufacturerPageable(Pageable pageable) {
        Direction direction = Direction.valueOf(manufacturerSortDirection);
        Sort defaultSort = Sort.by(direction, manufacturerSortBy);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(defaultSort));
    }
}
