package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@Controller
@ResponseBody
@RequestMapping("/v1/manufacturers/models")
@RequiredArgsConstructor
@Validated
public class ModelController {
    
    public static final String NAME_FIELD = "name";
    
    private final ModelService modelService;
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid ModelDto model) {
        ModelDto createdModel = modelService.save(model);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(createdModel.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
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
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
    }
}
