package ua.com.foxminded.vehicles.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.Category;
import ua.com.foxminded.vehicles.model.Manufacturer;
import ua.com.foxminded.vehicles.model.Model;
import ua.com.foxminded.vehicles.model.Vehicle;
import ua.com.foxminded.vehicles.service.VehicleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
public class VehicleController extends DefaultController {
    
    private static final String CATEGORY_NAME = "category";
    private static final String PRODUCTION_YEAR_FIELD = "productionYear";
    private static final String MODEL_NAME = "model";
    private static final String MANUFACTURER_NAME = "manufacturer";
    private static final String MIN_PRODUCTION_YEAR = "minYear";
    private static final String MAX_PRODUCTION_YEAR = "maxYear";
    private static final String PRODUCTION_YEAR = "year";
    
    private final VehicleService vehicleService;
    
    @GetMapping(value = "/vehicles", params = {MODEL_NAME})
    public Page<Vehicle> getByModel(@RequestParam(MODEL_NAME) String modelName, Pageable pageable) {
        return vehicleService.getByModel(modelName, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {CATEGORY_NAME})
    public Page<Vehicle> getByCategory(@RequestParam(CATEGORY_NAME) String categoryName, Pageable pageable) {
        return vehicleService.getByCategory(categoryName, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {MANUFACTURER_NAME, MAX_PRODUCTION_YEAR}) 
    public Page<Vehicle> getByManufacturerAndMaxProductionYear(
            @NotBlank @RequestParam(MANUFACTURER_NAME) String manufacturerName, 
            @RequestParam(MAX_PRODUCTION_YEAR) int maxYear, 
            @PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF)
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMaxYear(manufacturerName, maxYear, pageable);
    }
    
    @GetMapping(value = "/vehicles", params = {MANUFACTURER_NAME, MIN_PRODUCTION_YEAR}) 
    public Page<Vehicle> getByManufacturerAndMinProductionYear(
            @NotBlank @RequestParam(MANUFACTURER_NAME) String manufacturerName, 
            @RequestParam(MIN_PRODUCTION_YEAR) int minYear, 
            @PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF)
            @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMinYear(manufacturerName, minYear, pageable);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public Vehicle save(@PathVariable(MANUFACTURER_NAME) String manufacturerName, 
                        @PathVariable(MODEL_NAME) String modelName, 
                        @PathVariable(PRODUCTION_YEAR) int productionYear, 
                        @RequestBody Set<Category> categories) {
       
        Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
        Model model = Model.builder().name(modelName).build();
        
        Vehicle vehicle = Vehicle.builder().productionYear(productionYear)
                                           .manufacturer(manufacturer)
                                           .model(model)
                                           .categories(categories).build();
        return vehicleService.save(vehicle);
    }
    
    @GetMapping("/vehicles/page")
    public Page<Vehicle> getAll(@PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF) 
                                @SortDefault(sort = PRODUCTION_YEAR_FIELD, direction = Sort.Direction.DESC)
                                Pageable pageable) {
        return vehicleService.getAll(pageable);
    }
    
    @GetMapping("/vehicles/{id}")
    public Vehicle getById(@PathVariable @NotBlank String id) {
        return vehicleService.getById(id);
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public Vehicle update(@PathVariable(MANUFACTURER_NAME) @NotBlank String manufacturerName, 
                          @PathVariable(MODEL_NAME) @NotBlank String modelName,
                          @PathVariable(PRODUCTION_YEAR) @NotBlank int productionYear,
                          @RequestBody @Valid Vehicle vehicle) {
        vehicle.setManufacturer(Manufacturer.builder().name(manufacturerName).build());
        vehicle.setModel(Model.builder().name(modelName).build());
        vehicle.setProductionYear(productionYear);
        return vehicleService.update(vehicle);
    }
    
    @DeleteMapping("/vehicles/{id}")
    public void deleteById(@PathVariable @NotBlank String id) {
        vehicleService.deleteById(id);
    }
}
