package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import ua.com.foxminded.vehicles.config.PageSortConfig;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@Controller
@ResponseBody
@RequestMapping("/v1")
@RequiredArgsConstructor
@Validated
public class ModelController {
    
    private final ModelService modelService;
    private final PageSortConfig sortConfig;
    
    @PostMapping("/models")
    public ResponseEntity<String> save(@RequestBody @Valid ModelDto model) {
        ModelDto createdModel = modelService.save(model);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(createdModel.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/models/{name}")
    public ModelDto getByName(@PathVariable String name) {
        return modelService.getByName(name);
    }
    
    @GetMapping("/models")
    public Page<ModelDto> getAll(Pageable pageable) {
        Pageable pageableDef = setDefaults(pageable);
        return modelService.getAll(pageableDef);
    }
    
    @DeleteMapping("/models/{name}")
    public void deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
    }
    
    private Pageable setDefaults(Pageable pageable) {
        Direction direction = sortConfig.getModelSortDirection();
        String sortParameter = sortConfig.getModelSortParameter();
        Sort sortDef = Sort.by(direction, sortParameter);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(sortDef));
    }
}
