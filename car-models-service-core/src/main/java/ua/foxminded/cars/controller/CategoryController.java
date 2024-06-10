package ua.foxminded.cars.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.dto.CategoryDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CategoryDto>> getAllCategories(Pageable pageable) {
    Page<CategoryDto> pageWithCategories = categoryService.getAllCategories(pageable);
    return ResponseEntity.ok(pageWithCategories);
  }
}
