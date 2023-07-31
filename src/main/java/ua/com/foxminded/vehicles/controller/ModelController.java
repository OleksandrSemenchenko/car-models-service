package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.SEE_OTHER;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
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
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@Controller
@ResponseBody
@RequestMapping("/v1")
@RequiredArgsConstructor
@Validated
public class ModelController {
    
    @Value("${model.sort-parameter}")
    private String modelSortParameter;
    
    @Value("${model.sort-direction}")
    private String modelSortDirection;
    
    private final ModelService modelService;
    
    @PostMapping("/models")
    public ResponseEntity<String> save(@RequestBody @Valid ModelDto model) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(model.getName())
                                                  .toUri();
        
        if (modelService.existsByName(model.getName())) {
            return ResponseEntity.status(SEE_OTHER)
                                 .location(location).build();
        } else {
            modelService.save(model);
            return ResponseEntity.created(location).build();
        }
    }
    
    @GetMapping("/models/{name}")
    public ResponseEntity<ModelDto> getByName(@PathVariable String name) {
        ModelDto model = modelService.getByName(name);
        return ResponseEntity.ok(model);
    }
    
    @GetMapping("/models")
    public Page<ModelDto> getAll(Pageable pageable) {
        Pageable pageableDef = setDefaults(pageable);
        return modelService.getAll(pageableDef);
    }
    
    @DeleteMapping("/models/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaults(Pageable pageable) {
        Direction direction = Direction.valueOf(modelSortDirection);
        Sort sortDef = Sort.by(direction, modelSortParameter);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(sortDef));
    }
}
