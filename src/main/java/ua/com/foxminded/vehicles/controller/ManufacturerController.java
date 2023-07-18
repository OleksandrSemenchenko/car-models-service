package ua.com.foxminded.vehicles.controller;

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
import ua.com.foxminded.vehicles.model.Manufacturer;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@Validated
public class ManufacturerController extends DefaultController {
    
    public static final String NAME_FIELD = "name";
    public static final String NEW_NAME = "newName";
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public Manufacturer getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping("/page")
    public Page<Manufacturer> getAll(@PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF) 
                                     @SortDefault(sort = NAME_FIELD, direction = Sort.Direction.DESC)
                                     Pageable pageable) {
        return manufacturerService.getAll(pageable);
    }
    
    @PostMapping("/manufacturer")
    public Manufacturer save(@Valid @RequestBody Manufacturer manufacturer) {
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
