package ua.com.foxminded.vehicles.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.model.Category;
import ua.com.foxminded.vehicles.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories/")
public class CategoryController extends DefaultController {
    
    public static final String NEW_NAME = "newName";
    public static final String NAME_FIELD = "name";
    
    private final CategoryService categoryService;
    
    @PostMapping("/category")
    public Category save(@Valid @RequestBody Category model) {
        return categoryService.save(model);
    }
    
    @GetMapping("/{name}")
    public Category getByName(@PathVariable String name) {
        return categoryService.getByName(name);
    }
    
    @GetMapping("/page")
    public Page<Category> getAll(@PageableDefault(page = PAGE_NUMBER_DEF, size = PAGE_SIZE_DEF) 
                                 @SortDefault(sort =  NAME_FIELD, direction = Sort.Direction.DESC)
                                 Pageable pageable) {
        return categoryService.getAll(pageable);
    }
    
    @PutMapping("/{name}")
    public Category updateName(@PathVariable String name, 
                               @RequestParam(NEW_NAME) String newName) {
        return categoryService.updateName(newName, name);
    }
    
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable String name) {
        categoryService.deleleteByName(name);
    }
}
