/*
 * Copyright 2023 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.foxminded.cars.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.ModelService;
import ua.foxminded.cars.service.dto.ModelDto;

@RestController
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
public class ModelController {

  private static final String V1 = "v1";
  private static final String MODEL_ID_PATH = "/models/{id}";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MODELS_PATH = "/models";

  private final ModelService modelService;

  @GetMapping("/manufacturers/{manufacturer}/models/{name}/{year}")
  @Operation(
      summary = "Get a model by its manufacturer, name and year",
      operationId = "getModelByManufacturerAndNameAndYear",
      description =
          "Seach for and retrieve a model by its manufacturer, name and year from the database",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The model",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ModelDto.class)),
                    examples =
                        @ExampleObject(
                            name = "model",
                            value =
                                "{\"id\": \"1\", "
                                    + "\"name\": \"A7\", "
                                    + "\"year\": \"2023\", "
                                    + "\"manufacturer\": \"Audi\", "
                                    + "\"categories\": [\"SUV\"]}"))),
        @ApiResponse(
            responseCode = "400",
            description = "The model year must be positive"),
        @ApiResponse(
            responseCode = "404",
            description = "The model has not been found")
      })
  public ModelDto getByManufacturerAndNameAndYear(
      @PathVariable String manufacturer,
      @PathVariable String name,
      @PathVariable @Positive int year) {
    return modelService.getModel(manufacturer, name, year);
  }

  @Operation(
    summary = "Search models",
    operationId = "searchModels",
    description =
      "Seach for models in the database by optional parameters if no one is specified, "
        + "retrieve all models",
    tags = "model",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "The sorted page of models",
        useReturnTypeSchema = true),
      @ApiResponse(
        responseCode = "400",
        description = "The maxYear, minYear, year parameters must be positive")
    })
  @GetMapping(value = V1 + MODELS_PATH)
  public Page<ModelDto> searchModels(@Valid @ParameterObject SearchFilter searchFilter,
                                     @ParameterObject Pageable pageRequest) {
    return modelService.searchModel(searchFilter, pageRequest);
  }

  @GetMapping("/models/{id}")
  @Operation(
      summary = "Get a model by its id",
      operationId = "getModelById",
      description = "Search for and retrieve a model from the database by its name",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The model",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ModelDto.class)),
                    examples =
                        @ExampleObject(
                            name = "model",
                            value =
                                "{\"id\": \"1\", "
                                    + "\"name\": \"A7\", "
                                    + "\"year\": \"2023\", "
                                    + "\"manufacturer\": \"Audi\", "
                                    + "\"categories\": [\"Sedan\"]}"))),
        @ApiResponse(
            responseCode = "404",
            description = "The model has not been found")
      })
  public ModelDto getById(@PathVariable UUID id) {
    return modelService.getModelById(id);
  }

  //TODO
  @Operation(
      summary = "Create a model",
      operationId = "createModel",
      description = "Creates a car model",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "The model has been created",
            headers = @Header(name = "Location", description = "/models/{id}")),
        @ApiResponse(
            responseCode = "400",
            description =
                "The model must have a non-empty and non-null category and "
                    + "a model year must be positive"),
        @ApiResponse(
            responseCode = "404",
            description = "The model component has not been found"),
        @ApiResponse(
            responseCode = "409",
            description = "Such model already exists")
      })
  @PostMapping("/models")
  public ResponseEntity<Void> createModel(
      @PathVariable String manufacturer,
      @PathVariable String name,
      @PathVariable @Positive int year,
      @RequestBody @Valid ModelDto modelDto) {
    modelDto.setManufacturer(manufacturer);
    modelDto.setName(name);
    modelDto.setYear(year);

    ModelDto persistedModel = modelService.createModel(modelDto);
    URI location =
        ServletUriComponentsBuilder.fromCurrentServletMapping()
            .path("/v1/models/{id}")
            .buildAndExpand(persistedModel.getId())
            .toUri();
    return ResponseEntity.created(location).build();
  }

  @Operation(
      summary = "Updates a model",
      operationId = "updateModel",
      description = "Updates a car model by a provided request body",
      tags = "model",
      responses = {
        @ApiResponse(responseCode = "200", description = "The model data has been updated"),
        @ApiResponse(
          responseCode = "400",
          description = "Bad request",
          content = @Content(examples = @ExampleObject("""
            {
              "timestamp": "2024-05-12T12:04:46.015013732",
              "errorCode": 400,
              "details": {
                "updateModel.year": "must be greater than 0"
              }
            }
            """))),
        @ApiResponse(
            responseCode = "404",
            description = "A model has not been found",
            content = @Content(examples = @ExampleObject("""
              {
                "timestamp": "2024-05-12T10:36:28.639097556",
                "errorCode": 404,
                "details": "The model with manufacturer 'Chevrolet', name 'Malibu' and year '20292' not found"
              }
              """)))
      })
  @ResponseStatus(OK)
  @PutMapping(value = V1 + MODEL_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateModel(
      @Parameter(description = "A manufacturer name", example = "BMW")
      @PathVariable String manufacturer,
      @Parameter(description = "A model name", example = "x7")
      @PathVariable String name,
      @Parameter(description = "A year of a model", example = "'2023")
      @PathVariable @Positive int year,
      @RequestBody ModelDto modelDto) {
    modelDto.setYear(year);
    modelDto.setManufacturer(manufacturer);
    modelDto.setName(name);
    modelService.updateModel(modelDto);
  }

  @DeleteMapping(value = V1 + MODEL_ID_PATH)
  @ResponseStatus(NO_CONTENT)
  @Operation(
      summary = "Delete a model by its ID",
      operationId = "deleteModelById",
      description = "Search for and delete a model from a database",
      tags = "model",
      responses = {
        @ApiResponse(responseCode = "204", description = "The model has been deleted"),
        @ApiResponse(
            responseCode = "404",
            description = "The model not found",
            content = @Content(examples = @ExampleObject("""
              {
                "timestamp": "2024-05-12T10:20:47.550424959",
                "errorCode": 404,
                "details": "The model with id=d2541b4c-4882-439e-ab9c-f0c5a3924c47 not found"
              }
              """
            )))
      })
  public void deleteModelById(
    @Parameter(description = "a model ID", example = "52096834-48af-41d1-b422-93600eff629a")
    @PathVariable UUID id) {
    modelService.deleteModelById(id);
  }
}
