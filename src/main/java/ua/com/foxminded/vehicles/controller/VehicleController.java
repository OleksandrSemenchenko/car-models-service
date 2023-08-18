package ua.com.foxminded.vehicles.controller;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.service.VehicleService;
import ua.com.foxminded.vehicles.specification.SearchFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
public class VehicleController {
    
    @Value("${application.sort.vehicle.by}")
    private String vehicleSortBy;
    
    @Value("${application.sort.vehicle.direction}")
    private Direction vehicleSortDirection;
    
    private final VehicleService vehicleService;
    
    @GetMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public Optional<VehicleDto> searchByManufacturerAndModelAndYear(@PathVariable String manufacturer, 
                                                                    @PathVariable String model, 
                                                                    @PathVariable @Positive int year) {
        return vehicleService.getByManufacturerAndModelAndYear(manufacturer, model, year);
    }
    
    @GetMapping("/vehicles")
    public Page<VehicleDto> search(@Valid SearchFilter searchFilter, Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return vehicleService.search(searchFilter, pageRequest);
    }
    
    @GetMapping("/vehicles/{id}")
    public Optional<VehicleDto> getById(@PathVariable String id) {
        return vehicleService.getById(id);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<Void> save(@PathVariable String manufacturer, 
                                     @PathVariable String model, 
                                     @PathVariable @Positive int year, 
                                     @RequestBody @Valid VehicleDto vehicle) {
        vehicle.setManufacturer(manufacturer);
        vehicle.setModel(model);
        vehicle.setYear(year);
        
        VehicleDto persistedVehicle = vehicleService.save(vehicle);
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
                                                  .path("/v1/vehicles/{id}")
                                                  .buildAndExpand(persistedVehicle.getId())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public void update(@PathVariable String manufacturer, 
                       @PathVariable String model, 
                       @PathVariable @Positive int year,  
                       @RequestBody @Valid VehicleDto vehicleDto) {
        vehicleService.update(manufacturer, model, year, vehicleDto);
    }
    
    @DeleteMapping("/vehicles/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        vehicleService.deleteById(id);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaulSort = Sort.by(vehicleSortDirection, vehicleSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaulSort));
        }
        return pageRequest;
    }
}
