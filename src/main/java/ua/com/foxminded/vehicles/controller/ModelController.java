package ua.com.foxminded.vehicles.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@Controller
@ResponseBody
@RequestMapping("/v1/manufacturers/models")
@RequiredArgsConstructor
@Validated
public class ModelController extends ExceptionHandlerController {
    
    public static final String NAME_FIELD = "name";
    
    private final ModelService modelService;
    
    @PostMapping("/{model}")
    public void save(@PathVariable String modelName) {
        ModelDto model = ModelDto.builder().name(modelName).build();
        modelService.save(model);
    }
    
    @GetMapping("/{name}")
    public ModelDto getByName(@PathVariable String name) {
        return modelService.getByName(name);
    }
    
    @GetMapping
    public Page<ModelDto> getAll(@SortDefault(sort = NAME_FIELD, direction = Sort.Direction.DESC)
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
