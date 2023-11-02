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
import ua.com.foxminded.dto.ManufacturerDto;
import ua.com.foxminded.exception.ErrorResponse;
import ua.com.foxminded.service.ManufacturerService;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ManufacturerController {
    
    @Value("${application.sort.manufacturer.by}")
    private String manufacturerSortBy;
    
    @Value("${application.sort.manufacturer.direction}")
    private Direction manufacturerSortDirection;
    
    private final ManufacturerService manufacturerService;
    
    @GetMapping("/{name}")
    @Operation(summary = "Get a manufacturer by its name", operationId = "getManufacturerByName", 
               description = "Search for and retrieve a manufacturer from the database",
               tags = "manufacturer",
               responses = {
                       @ApiResponse(responseCode = "200", description = "Ok", content = @Content(
                               mediaType = "application/json", 
                               array = @ArraySchema(schema = @Schema(implementation = ManufacturerDto.class)),
                               examples = @ExampleObject(name = "manufacturer", value = "{\"name\": \"Audi\"}"))),
                       @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(
                               mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
               })
    public ManufacturerDto getByName(@PathVariable String name) {
        return manufacturerService.getByName(name);
    }
    
    @GetMapping
    @Operation(summary = "Get all manufacturers", operationId = "getAllManufacturers", 
               description = "Retrieve all manufacturers from the database",
               tags = "manufacturer",
               responses = @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true))
    public Page<ManufacturerDto> getAll(@ParameterObject Pageable pageRequest) {
        pageRequest = setDefaultSortIfNeeded(pageRequest);
        return manufacturerService.getAll(pageRequest);
    }
    
    @PostMapping
    @Operation(summary = "Save a manufacturer", operationId = "saveManufacturer", 
               description = "Search for such manufacturer in the database if it is missing then persist it",
               tags = "manufacturer",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                       mediaType = "application/json", 
                       schema = @Schema(implementation = ManufacturerDto.class), 
                       examples = @ExampleObject(name = "manufacturer", value = "{\"name\": \"Audi\"}"))),
               responses = {
                       @ApiResponse(responseCode = "201", description = "Created", 
                                    headers = @Header(name = "Location", description = "/v1/manufacturers/{name}")),
                       @ApiResponse(responseCode = "409", description = "Conflict", 
                                    content = @Content(mediaType = "application/json", 
                                                       schema = @Schema(implementation = ErrorResponse.class))) 
                })
    public ResponseEntity<Void> save(@RequestBody @Valid ManufacturerDto manufacturer) {
        manufacturerService.save(manufacturer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{name}")
                                                  .buildAndExpand(manufacturer.getName())
                                                  .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @DeleteMapping("/{name}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Delete a manufacturer by its name", operationId = "deleteManufacturerByName", 
               description = "Seach for and delete a manufacturer from the database by its name",
               tags = "manufacturer",
               responses = {
                       @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(
                               mediaType = "application/json", 
                               schema = @Schema(implementation = ErrorResponse.class))), 
                       @ApiResponse(responseCode = "405", description = "Method Not Allowed", content = @Content(
                               mediaType = "application/json", 
                               schema = @Schema(implementation = ErrorResponse.class)))
               })
    public void deleteByName(@PathVariable String name) {
        manufacturerService.deleteByName(name);
    }
    
    private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
        if (pageRequest.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(manufacturerSortDirection, manufacturerSortBy);
            return PageRequest.of(pageRequest.getPageNumber(), 
                                  pageRequest.getPageSize(),
                                  pageRequest.getSortOr(defaultSort));
        }
        return pageRequest;
    }
}
