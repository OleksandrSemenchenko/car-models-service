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
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {
    
    @Value("${application.sort.category.by}")
    private String categorySortBy;
    
    @Value("${application.sort.category.direction}")
    private String categorySortDirection;
    
    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid CategoryDto category) {
        categoryService.save(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                                  .path("/{name}")
                                                  .buildAndExpand(category.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<CategoryDto> getByName(@PathVariable String name) {
        CategoryDto category = categoryService.getByName(name);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping
    public Page<CategoryDto> getAll(Pageable pageable) {
        Pageable defaultPageable = setDefaultsForCategoryPageable(pageable);
        return categoryService.getAll(defaultPageable);
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        categoryService.deleleteByName(name);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable setDefaultsForCategoryPageable(Pageable pageable) {
        Direction direction = Direction.valueOf(categorySortDirection);
        Sort defaultSort = Sort.by(direction, categorySortBy);
        return PageRequest.of(pageable.getPageNumber(), 
                              pageable.getPageSize(),
                              pageable.getSortOr(defaultSort));
    }
}
