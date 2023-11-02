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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.dto.ModelDto;
import ua.com.foxminded.exception.ErrorResponse;
import ua.com.foxminded.service.ModelService;
import ua.com.foxminded.specification.SearchFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class ModelController {
    
    @Value("${application.sort.model.by}")
    private String modelSortBy;
    
    @Value("${application.sort.model.direction}")
    private Direction modelSortDirection;
    
    private final ModelService modelService;
    
    @GetMapping("/manufacturers/{manufacturer}/models/{name}/{year}")
    @Operation(summary = "Get a model by its manufacturer, name and year", 
               operationId = "getModelByManufacturerAndNameAndYear",
               description = "Seach for and retrieve a model by its manufacturer, name and year from the database",
               tags = "model",
               responses = {
                       @ApiResponse(responseCode = "200", description = "Ok", content = @Content(
                               mediaType = "application/json", 
                               array = @ArraySchema(schema = @Schema(implementation = ModelDto.class)), 
                               examples = @ExampleObject(name = "model", 
                                                         value = "{\"id\": \"1\", " +
                                                                  "\"name\": \"A7\", " +
                                                                  "\"year\": \"2023\", " +
                                                                  "\"manufacturer\": \"Audi\", " +
                                                                  "\"categories\": [\"SUV\"]}"))),
                       @ApiResponse(responseCode = "404", description = "Not Found", content = {
                               @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ErrorResponse.class))
                       })
               })
    public ModelDto getByManufacturerAndNameAndYear(@PathVariable String manufacturer, 
                                                              @PathVariable String name, 
                                                              @PathVariable @Positive int year) {
        return modelService.getByManufacturerAndNameAndYear(manufacturer, name, year);
    }
    
    @GetMapping("/models")
    @Operation(summary = "Search models", operationId = "searchModels", 
               description = "Seach for models in the database by optional parameters if no one is specified, " +
                             "retrieve all models",
               tags = "model",
               responses = @ApiResponse(responseCode = "200", useReturnTypeSchema = true))
    public Page<ModelDto> search(@Valid @ParameterObject SearchFilter searchFilter, 
                                 @ParameterObject Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return modelService.search(searchFilter, pageRequest);
    }
    
    @GetMapping("/models/{id}")
    @Operation(summary = "Get a model by its id", operationId = "getModelById", 
               description = "Seach for and revrieve a model from the database by its name",
               tags = "model",
               responses = {
                       @ApiResponse(responseCode = "200", description = "Ok", content = @Content(
                               mediaType = "application/json", 
                               array = @ArraySchema(schema = @Schema(implementation = ModelDto.class)), 
                               examples = @ExampleObject(name = "model", value = "{\"id\": \"1\", " +
                                                                                  "\"name\": \"A7\", " +
                                                                                  "\"year\": \"2023\", " +
                                                                                  "\"manufacturer\": \"Audi\", " +
                                                                                  "\"categories\": [\"Sedan\"]}"))),
                       @ApiResponse(responseCode = "404", description = "Not Found", 
                             content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = ErrorResponse.class)))
               })
    public ModelDto getById(@PathVariable String id) {
        return modelService.getById(id);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{name}/{year}")
    @Operation(summary = "Save a model", operationId = "saveModel", 
               description = "Seach for a model by its manufacturer, name and year in the database if it is missing, " +
                              "persist it",
               tags = "model",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                       mediaType = "application/json", 
                       schema = @Schema(implementation = ModelDto.class), 
                       examples = @ExampleObject(name = "model", value = "{\"id\": null, " +
                                                                          "\"name\": null, " +
                                                                          "\"year\": null, " +
                                                                          "\"manufacturer\": null, " +
                                                                          "\"categories\": [\"Sedan\"]}"))),
               responses = {
                       @ApiResponse(responseCode = "201", description = "Created", 
                                    headers = @Header(name = "Location", description = "/v1/models/{id}")), 
                       @ApiResponse(responseCode = "404", description = "Not Found", 
                                    content = @Content(mediaType = "application/json", 
                                                       schema = @Schema(implementation = ErrorResponse.class))), 
                       @ApiResponse(responseCode = "409", description = "Conflict", 
                                    content = @Content(mediaType = "application/json", 
                                                       schema = @Schema(implementation = ErrorResponse.class)))
               })
    public ResponseEntity<Void> save(@PathVariable String manufacturer, 
                                     @PathVariable String name, 
                                     @PathVariable @Positive int year, 
                                     @RequestBody @Valid ModelDto modelDto) {
        modelDto.setManufacturer(manufacturer);
        modelDto.setName(name);
        modelDto.setYear(year);
        
        ModelDto persistedModel = modelService.save(modelDto);
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
                                                  .path("/v1/models/{id}")
                                                  .buildAndExpand(persistedModel.getId())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{name}/{year}")
    @Operation(summary = "Update a model", operationId = "updateModel", 
               description = "Seach for a model by its manufacturer, name and year in the database if it exists, " +
                             "update it",
               tags = "model",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                       schema = @Schema(implementation = ModelDto.class), 
                       examples = @ExampleObject(name = "model", 
                                                 value = "{\"id\": null, "
                                                        + "\"name\": null, " 
                                                        + "\"year\": null, " 
                                                        + "\"manufacturer\": null, "
                                                        + "\"categories\": [\"Sedan\"]}"))),
               responses = {
                       @ApiResponse(responseCode = "200", description = "Ok"), 
                       @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(
                               mediaType = "application/json", 
                               schema = @Schema(implementation = ErrorResponse.class)))
               })
    public void update(@PathVariable String manufacturer, 
                       @PathVariable String name, 
                       @PathVariable @Positive int year,  
                       @RequestBody @Valid ModelDto modelDto) {
        modelDto.setManufacturer(manufacturer);
        modelDto.setName(name);
        modelDto.setYear(year);
        modelService.update(modelDto);
    }
    
    @DeleteMapping("/models/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Delete a model by its id", operationId = "deleteModelById", 
               description = "Search for and delete from the database a model by its id",
               tags = "model",
               responses = @ApiResponse(responseCode = "404", description = "Not Found", 
                                        content = @Content(mediaType = "application/json", 
                                                           schema = @Schema(implementation = ErrorResponse.class))))
    public void deleteById(@PathVariable String id) {
        modelService.deleteById(id);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaulSort = Sort.by(modelSortDirection, modelSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaulSort));
        }
        return pageRequest;
    }
}
