package ua.com.foxminded.vehicles.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.CategoryDto;
import ua.com.foxminded.vehicles.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController extends ExceptionHandlerController {
    
    public static final String CATEGORY = "category";
    public static final String NAME = "newName";
    public static final String NAME_FIELD = "name";
    
    private final CategoryService categoryService;
    
    @PostMapping("/{category}")
    public CategoryDto save(@PathVariable(CATEGORY) String categoryName) {
        CategoryDto category = CategoryDto.builder().name(categoryName).build();
        return categoryService.save(category);
    }
    
    @GetMapping("/{name}")
    public CategoryDto getByName(@PathVariable String name) {
        return categoryService.getByName(name);
    }
    
    @GetMapping
    public Page<CategoryDto> getAll(@SortDefault(sort =  NAME_FIELD, direction = Sort.Direction.DESC)
                                    Pageable pageable) {
        return categoryService.getAll(pageable);
    }
    
    @PutMapping("/{name}")
    public CategoryDto updateName(@PathVariable String name, 
                                  @RequestParam(NAME) String newName) {
        return categoryService.updateName(newName, name);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        categoryService.deleleteByName(name);
    }
}
