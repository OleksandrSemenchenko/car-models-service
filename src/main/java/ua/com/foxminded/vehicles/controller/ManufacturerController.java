package ua.com.foxminded.vehicles.controller;

import java.util.List;

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

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.Manufacturer;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@Validated
public class ManufacturerController extends DefaultController {
    
    public static final String NEW_NAME_PARAMETER = "newName";
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    public Manufacturer getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping("/list")
    public List<Manufacturer> getAll() {
        return manufacturerService.getAll();
    }
    
    @PostMapping("/manufacturer")
    public Manufacturer save(@RequestBody Manufacturer manufacturer) {
        return manufacturerService.save(manufacturer);
    }
    
    @PutMapping("/{name}")
    public void updateName(@PathVariable String name, 
                           @RequestParam (NEW_NAME_PARAMETER) @NotBlank String newName) {
        manufacturerService.updateName(newName, name);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
    }
}
