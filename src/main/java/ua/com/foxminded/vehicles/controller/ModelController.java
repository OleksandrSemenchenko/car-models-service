package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ModelController {
    
    @Value("${application.sort.model.by}")
    private String modelSortBy;
    
    @Value("${application.sort.model.direction}")
    private String modelSortDirection;
    
    private final ModelService modelService;
    
    @PostMapping("/models")
    public ResponseEntity<String> save(@RequestBody @Valid ModelDto model) {
        modelService.save(model);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(model.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/models/{name}")
    public ResponseEntity<ModelDto> getByName(@PathVariable String name) {
        ModelDto model = modelService.getByName(name);
        return ResponseEntity.ok(model);
    }
    
    @GetMapping("/models")
    public Page<ModelDto> getAll(Pageable pageable) {
        Pageable defaultPageable = setDefaultsForModelPageable(pageable);
        return modelService.getAll(defaultPageable);
    }
    
    @DeleteMapping("/models/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaultsForModelPageable(Pageable pageable) {
        Direction direction = Direction.valueOf(modelSortDirection);
        Sort defaultSort = Sort.by(direction, modelSortBy);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(defaultSort));
    }
}
