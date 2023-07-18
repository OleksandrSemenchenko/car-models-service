package ua.com.foxminded.vehicles.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.Model;
import ua.com.foxminded.vehicles.service.ModelService;

@Controller
@ResponseBody
@RequestMapping("/v1/manufacturers/models")
@RequiredArgsConstructor
@Validated
public class ModelController extends DefaultController {
    
    public static final String NAME_FIELD = "name";
    
    private final ModelService modelService;
    
    @PostMapping("/model")
    public void save(@Valid @RequestBody Model model) {
        modelService.save(model);
    }
    
    @GetMapping("/{name}")
    public Model getByName(@PathVariable String name) {
        return modelService.getByName(name);
    }
    
    @GetMapping("/page")
    public Page<Model> getAll(@PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF) 
                              @SortDefault(sort = NAME_FIELD, direction = Sort.Direction.DESC)
                              Pageable pageable) {
        return modelService.getAll(pageable);
    }
    
    @PutMapping("/{name}")
    public void updateName(@NotBlank @RequestParam String newName, @PathVariable String name) {
        modelService.updateName(newName, name);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
    }
}
