package ua.com.foxminded.vehicles.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.ManufacturerDto;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@Validated
public class ManufacturerController extends ExceptionHandlerController {
    
    public static final String MANUFACTURER = "manufacturer";
    public static final String NAME_FIELD = "name";
    public static final String NEW_NAME = "newName";
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public ManufacturerDto getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping
    public Page<ManufacturerDto> getAll(@SortDefault(sort = NAME_FIELD, direction = Sort.Direction.DESC)
                                        Pageable pageable) {
        return manufacturerService.getAll(pageable);
    }
    
    @PostMapping("/{manufacturer}")
    public ManufacturerDto save(@PathVariable(MANUFACTURER) String manufacturerName) {
        ManufacturerDto manufacturer = ManufacturerDto.builder().name(manufacturerName).build();
        return manufacturerService.save(manufacturer);
    }
    
    @PutMapping("/{name}")
    public void updateName(@PathVariable String name, 
                           @RequestParam (NEW_NAME) @NotBlank String newName) {
        manufacturerService.updateName(newName, name);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
    }
}
