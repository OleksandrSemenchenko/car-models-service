package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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
    
    public static final String NAME_FIELD = "name";
    
    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid CategoryDto category) {
        CategoryDto createdCategory = categoryService.save(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                                  .path("/{name}")
                                                  .buildAndExpand(createdCategory.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/{name}")
    public CategoryDto getByName(@PathVariable String name) {
        return categoryService.getByName(name);
    }
    
    @GetMapping
    public Page<CategoryDto> getAll(@SortDefault(sort = NAME_FIELD, direction = Sort.Direction.DESC)
                                    Pageable pageable) {
        return categoryService.getAll(pageable);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        categoryService.deleleteByName(name);
    }
}
