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
package ua.com.foxminded.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
import ua.com.foxminded.dto.ModelNameDto;
import ua.com.foxminded.exceptionhandler.ErrorResponse;
import ua.com.foxminded.service.ModelNameService;

@RestController
@RequestMapping("/v1/model-names")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ModelNameController {

  @Value("${application.sort.model-name.by}")
  private String modelNameSortBy;

  @Value("${application.sort.model-name.direction}")
  private Direction modelNameSortDirection;

  private final ModelNameService modelNameService;

  @PostMapping
  @Operation(
      summary = "Create a name of the models",
      operationId = "createModelName",
      description =
          "Seach a specified name of the models in the database if it is missing, persist it",
      tags = "model-name",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      schema = @Schema(implementation = ModelNameDto.class),
                      examples = @ExampleObject(name = "modelName", value = "{\"name\": \"A7\"}"))),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "The name of models has been created",
            headers = @Header(name = "Location", description = "/names/{name}")),
        @ApiResponse(
            responseCode = "400",
            description = "The name of models must be non-empty and non-null",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Such name of models already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<Void> create(@RequestBody @Valid ModelNameDto modelNameDto) {
    modelNameService.create(modelNameDto);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{name}")
            .buildAndExpand(modelNameDto.getName())
            .toUri();
    return ResponseEntity.created(location).build();
  }

  @GetMapping("/{name}")
  @Operation(
      summary = "Get a name of the models by the name",
      operationId = "getModelNameByName",
      description = "Seach for a name of the models in the database if it exists, retrieve it",
      tags = "model-name",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The name of models",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ModelNameDto.class)),
                    examples = @ExampleObject(name = "modelName", value = "{\"name\": \"A7\"}"))),
        @ApiResponse(
            responseCode = "404",
            description = "The name of models has not been found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ModelNameDto getByName(@PathVariable String name) {
    return modelNameService.getByName(name);
  }

  @GetMapping
  @Operation(
      summary = "Get all names of the models",
      operationId = "getAllModelNames",
      description = "Retrieve all names of the models from the database",
      tags = "model-name",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The sorted page of model names",
              useReturnTypeSchema = true))
  public Page<ModelNameDto> getAll(@ParameterObject Pageable pageRequest) {
    pageRequest = setDefaultSortIfNeeded(pageRequest);
    return modelNameService.getAll(pageRequest);
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(NO_CONTENT)
  @Operation(
      summary = "Delete a name of the models by the name",
      operationId = "deleteModelNameByName",
      description = "Search for and delete a name of the models from the database",
      tags = "model-name",
      responses = {
        @ApiResponse(responseCode = "204", description = "The name of models has been deleted"),
        @ApiResponse(
            responseCode = "404",
            description = "The name of models has not been found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "405",
            description = "The name of models has relations and cannot be removed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public void deleteByName(@PathVariable String name) {
    modelNameService.deleteByName(name);
  }

  private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
    if (pageRequest.getSort().isUnsorted()) {
      Sort defaultSort = Sort.by(modelNameSortDirection, modelNameSortBy);
      return PageRequest.of(
          pageRequest.getPageNumber(),
          pageRequest.getPageSize(),
          pageRequest.getSortOr(defaultSort));
    }
    return pageRequest;
  }
}
