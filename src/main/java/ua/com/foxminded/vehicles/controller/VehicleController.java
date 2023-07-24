package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.service.VehicleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
public class VehicleController {
    
    public static final String CATEGORY_NAME = "category";
    public static final String PRODUCTION_YEAR_FIELD = "productionYear";
    public static final String MODEL_NAME = "model";
    public static final String MANUFACTURER_NAME = "manufacturer";
    public static final String MIN_PRODUCTION_YEAR = "minYear";
    public static final String MAX_PRODUCTION_YEAR = "maxYear";
    public static final String PRODUCTION_YEAR = "year";
    
    private final VehicleService vehicleService;
    
    @GetMapping(value = "/vehicles", params = {MODEL_NAME})
    public Page<VehicleDto> getByModel(
            @RequestParam(MODEL_NAME) String modelName, 
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
            Pageable pageable) {
        return vehicleService.getByModel(modelName, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {CATEGORY_NAME})
    public Page<VehicleDto> getByCategory(
            @RequestParam(CATEGORY_NAME) String categoryName, 
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)   
            Pageable pageable) {
        return vehicleService.getByCategory(categoryName, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {MANUFACTURER_NAME, MAX_PRODUCTION_YEAR}) 
    public Page<VehicleDto> getByManufacturerAndMaxProductionYear(
            @NotBlank @RequestParam(MANUFACTURER_NAME) String manufacturerName, 
            @RequestParam(MAX_PRODUCTION_YEAR) int maxYear, 
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMaxYear(manufacturerName, maxYear, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {MANUFACTURER_NAME, MIN_PRODUCTION_YEAR}) 
    public Page<VehicleDto> getByManufacturerAndMinProductionYear(
            @NotBlank @RequestParam(MANUFACTURER_NAME) String manufacturerName, 
            @RequestParam(MIN_PRODUCTION_YEAR) int minYear, 
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMinYear(manufacturerName, minYear, pageable);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> save(@PathVariable(MANUFACTURER_NAME) String manufacturerName, 
                                       @PathVariable(MODEL_NAME) String modelName, 
                                       @PathVariable(PRODUCTION_YEAR) int productionYear, 
                                       @RequestBody VehicleDto vehicle, 
                                       HttpServletRequest request) {
        ManufacturerDto manufacturer = ManufacturerDto.builder().name(manufacturerName).build();
        ModelDto model = ModelDto.builder().name(modelName).build();
        vehicle.setManufacturer(manufacturer);
        vehicle.setModel(model);
        vehicle.setProductionYear(productionYear);

        VehicleDto createdVehicle = vehicleService.save(vehicle);
        
        URI location = UriComponentsBuilder.newInstance().scheme(request.getScheme())
                                                         .host(request.getServerName())
                                                         .port(request.getServerPort())
                                                         .path("/v1/vehicles/{id}")
                                                         .buildAndExpand(createdVehicle.getId())
                                                         .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/vehicles")
    public Page<VehicleDto> getAll(@SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
                                   Pageable pageable) {
        return vehicleService.getAll(pageable);
    }
    
    @GetMapping("/vehicles/{id}")
    public VehicleDto getById(@PathVariable @NotBlank String id) {
        return vehicleService.getById(id);
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> update(@PathVariable(MANUFACTURER_NAME) @NotBlank String manufacturerName, 
                                         @PathVariable(MODEL_NAME) @NotBlank String modelName,
                                         @PathVariable(PRODUCTION_YEAR) int productionYear,
                                         @RequestBody @Valid VehicleDto vehicle, 
                                         HttpServletRequest request) {
        vehicle.setManufacturer(ManufacturerDto.builder().name(manufacturerName).build());
        vehicle.setModel(ModelDto.builder().name(modelName).build());
        vehicle.setProductionYear(productionYear);
        VehicleDto updatedVehicle = vehicleService.update(vehicle);
        URI location = UriComponentsBuilder.newInstance().scheme(request.getScheme())
                                                         .host(request.getServerName())
                                                         .port(request.getServerPort())
                                                         .path("/v1/vehicles/{id}")
                                                         .buildAndExpand(updatedVehicle.getId())
                                                         .toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(responseHeaders, HttpStatus.ACCEPTED);
    }
    
    @DeleteMapping("/vehicles/{id}")
    public void deleteById(@PathVariable @NotBlank String id) {
        vehicleService.deleteById(id);
    }
}
