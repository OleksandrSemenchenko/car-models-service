package ua.com.foxminded.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.dto.CategoryDto;
import ua.com.foxminded.exception.ErrorResponse;
import ua.com.foxminded.service.CategoryService;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    
    @Value("${application.sort.category.by}")
    private String categorySortBy;
    
    @Value("${application.sort.category.direction}")
    private Direction categorySortDirection;
    
    private final CategoryService categoryService;
    
    @PostMapping
    @Operation(summary = "Create a category", operationId = "createCategory", 
               description = "Persist a model category data in the database",
               tags = "category",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                       content = @Content(examples = @ExampleObject(name = "category", 
                                                                    value = "{\"name\": \"SUV\"}"))), 
               responses = {
                       @ApiResponse(responseCode = "201", description = "The category has been created", 
                                    headers = @Header(name = "Location", description = "/categories/{name}")),
                       @ApiResponse(responseCode = "400", 
                                    description = "The name of category must be non-null or non-empty",
                                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                       @ApiResponse(responseCode = "409", description = "Such category already exists", 
                                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
               })
    public ResponseEntity<Void> create(@RequestBody @Valid CategoryDto category) {
        categoryService.create(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                                  .path("/{name}")
                                                  .buildAndExpand(category.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    
    @GetMapping("/{name}")
    @Operation(summary = "Get a category by its name", operationId = "getCategoryByName", 
               description = "Search for and retrieve a model category by its name from the database",
               tags = "category",
               responses = {
                       @ApiResponse(responseCode = "200", description = "The category", 
                                    content = @Content(array = @ArraySchema(schema = @Schema(
                                                               implementation = CategoryDto.class)),
                                                       examples = @ExampleObject(name = "category" , 
                                                                                 value = "{\"name\": \"SUV\"}"))),
                       @ApiResponse(responseCode = "404", description = "The category has not been found", 
                                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                })
    public CategoryDto getByName(@PathVariable String name) {
        return categoryService.getByName(name);
    }
    
    @GetMapping
    @Operation(summary = "Get all categories", operationId = "getAllCategories", 
               description = "Retrieve all model categories from the database", 
               tags = "category",
               responses = @ApiResponse(responseCode = "200", description = "The sorted page of categories", 
                                        useReturnTypeSchema = true))
    public Page<CategoryDto> getAll(@ParameterObject Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return categoryService.getAll(pageRequest);
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Delete a category by its name", operationId = "deleteCategoryByName", 
               description = "Search for and delete a model category from the database by its name",
               tags = "category",
               responses = {
                       @ApiResponse(responseCode = "204", description = "The category has been deleted"),
                       @ApiResponse(responseCode = "404", description = "The category has not been found", 
                                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))), 
                       @ApiResponse(responseCode = "405", 
                                    description = "The category has relations and cannot be removed")
               })
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
