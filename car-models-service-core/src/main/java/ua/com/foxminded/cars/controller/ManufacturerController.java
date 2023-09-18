package ua.com.foxminded.cars.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.net.URI;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.cars.dto.ManufacturerDto;
import ua.com.foxminded.cars.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {
    
    @Value("${application.sort.manufacturer.by}")
    private String manufacturerSortBy;
    
    @Value("${application.sort.manufacturer.direction}")
    private Direction manufacturerSortDirection;
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public Optional<ManufacturerDto> getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping
    public Page<ManufacturerDto> getAll(Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return manufacturerService.getAll(pageRequest);
    }
    
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ManufacturerDto manufacturer) {
        manufacturerService.save(manufacturer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(manufacturer.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    public void deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(manufacturerSortDirection, manufacturerSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaultSort));
        }
        return pageRequest;
    }
}
