package ua.foxminded.cars.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

/**
 * A REST controller to manage categories.
 *
 * @author Oleksandr Semenchenko
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @Operation(
      summary = "Gets all categories",
      operationId = "getAllCategories",
      description = "Gets all existing categories in the page format",
      tags = "category",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The page with categories",
            useReturnTypeSchema = true),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content")))
      })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CategoryDto>> getAllCategories(Pageable pageable) {
    Page<CategoryDto> pageWithCategories = categoryService.getAllCategories(pageable);
    return ResponseEntity.ok(pageWithCategories);
  }
}
