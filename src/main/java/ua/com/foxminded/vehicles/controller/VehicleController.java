package ua.com.foxminded.vehicles.controller;

import java.net.URI;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.service.VehicleService;
import ua.com.foxminded.vehicles.specification.SpecificationParameters;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
public class VehicleController {
    
    @Value("${application.sort.vehicle.by}")
    private String vehicleSortBy;
    
    @Value("${application.sort.vehicle.direction}")
    private String vehicleSortDirection;
    
    private final VehicleService vehicleService;
    
    
    @GetMapping("/vehicles")
    public Page<VehicleDto> search(@RequestParam(required = false) String model, 
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) String manufacturer,
                                   @RequestParam(required = false) Integer maxYear,
                                   @RequestParam(required = false) Integer minYear,
                                   Pageable pageable) {
        Pageable defaultPageable = setDefaultsForVehiclePageable(pageable);
        SpecificationParameters parameters = SpecificationParameters.builder().modelName(model)
                                                                    .categoryName(category)
                                                                    .manufacturerName(manufacturer)
                                                                    .maxYear(maxYear)
                                                                    .minYear(minYear).build();
        return vehicleService.searchByOptionalPredicates(parameters, defaultPageable);
    }
    
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDto> getById(@PathVariable String id) {
        VehicleDto vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(vehicle);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> save(@PathVariable String manufacturer, 
                                       @PathVariable String model, 
                                       @PathVariable int year, 
                                       @RequestBody VehicleDto vehicle, 
                                       HttpServletRequest request) {
        vehicle.setManufacturer(ManufacturerDto.builder().name(manufacturer).build());
        vehicle.setModel(ModelDto.builder().name(model).build());
        vehicle.setProductionYear(year);
        
        VehicleDto persistedVehicle = vehicleService.save(vehicle);
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
                                                  .path("/v1/vehicles/{id}")
                                                  .buildAndExpand(persistedVehicle.getId())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> update(@PathVariable String manufacturer, 
                                         @PathVariable String model,
                                         @PathVariable int year,
                                         @RequestBody @Valid VehicleDto vehicle, 
                                         HttpServletRequest request) {
        vehicle.setManufacturer(ManufacturerDto.builder().name(manufacturer).build());
        vehicle.setModel(ModelDto.builder().name(model).build());
        vehicle.setProductionYear(year);

        vehicleService.update(vehicle);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaultsForVehiclePageable(Pageable pageable) {
        Direction direction = Direction.valueOf(vehicleSortDirection);
        Sort defaulSort = Sort.by(direction, vehicleSortBy);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(defaulSort));
    }
}
