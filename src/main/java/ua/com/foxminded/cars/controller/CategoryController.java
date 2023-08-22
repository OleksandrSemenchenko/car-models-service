package ua.com.foxminded.cars.controller;

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
import ua.com.foxminded.cars.dto.CategoryDto;
import ua.com.foxminded.cars.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {
    
    @Value("${application.sort.category.by}")
    private String categorySortBy;
    
    @Value("${application.sort.category.direction}")
    private Direction categorySortDirection;
    
    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid CategoryDto category) {
        categoryService.save(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                                  .path("/{name}")
                                                  .buildAndExpand(category.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/{name}")
    public Optional<CategoryDto> getByName(@PathVariable String name) {
        return categoryService.getByName(name);
    }
    
    @GetMapping
    public Page<CategoryDto> getAll(Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return categoryService.getAll(pageRequest);
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    public void deleteByName(@PathVariable String name) {
        categoryService.deleleteByName(name);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(categorySortDirection, categorySortBy);
            pageRequest.getSortOr(defaultSort);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaultSort));
        } 
        return pageRequest;
    }
}
