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
import ua.com.foxminded.vehicles.dto.ModelNameDto;
import ua.com.foxminded.vehicles.service.ModelNameService;

@RestController
@RequestMapping("/v1/model-names")
@RequiredArgsConstructor
public class ModelNameController {
    
    @Value("${application.sort.model-name.by}")
    private String modelNameSortBy;
    
    @Value("${application.sort.model-name.direction}")
    private Direction modelNameSortDirection;
    
    private final ModelNameService modelNameService;
    
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ModelNameDto modelNameDto) {
        modelNameService.save(modelNameDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(modelNameDto.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/{name}")
    public Optional<ModelNameDto> getByName(@PathVariable String name) {
        return modelNameService.getByName(name);
    }
    
    @GetMapping
    public Page<ModelNameDto> getAll(Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return modelNameService.getAll(pageRequest);
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    public void deleteByName(@PathVariable String name) {
        modelNameService.deleteByName(name);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(modelNameSortDirection, modelNameSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaultSort));
        }
        return pageRequest;
    }
}
