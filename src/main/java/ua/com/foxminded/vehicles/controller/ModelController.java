package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.net.URI;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.service.ModelService;

@RestController
@RequestMapping("/v1/models")
@RequiredArgsConstructor
public class ModelController {
    
    @Value("${application.sort.model.by}")
    private String modelSortBy;
    
    @Value("${application.sort.model.direction}")
    private Direction modelSortDirection;
    
    private final ModelService modelService;
    
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ModelDto model) {
        modelService.save(model);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(model.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/{name}")
    public Optional<ModelDto> getByName(@PathVariable String name) {
        return modelService.getByName(name);
    }
    
    @GetMapping
    public Page<ModelDto> getAll(Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return modelService.getAll(pageRequest);
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    public void deleteByName(@PathVariable String name) {
        modelService.deleteByName(name);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(modelSortDirection, modelSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaultSort));
        }
        return pageRequest;
    }
}
