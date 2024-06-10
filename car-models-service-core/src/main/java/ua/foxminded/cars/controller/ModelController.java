package ua.foxminded.cars.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * A REST controller to manage models.
 *
 * @author Oleksandr Semenchenko
 */
@Tag(name = "ModelController", description = "Manages models")
@RestController
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
public class ModelController {

  private static final String V1 = "/v1";
  private static final String MODEL_ID_PATH = "/models/{id}";
  private static final String MODEL_PATH = "/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final String MODELS_PATH = "/models";

  private final ModelService modelService;

  @Operation(
      summary = "Get a model by manufacturer, name, and year",
      operationId = "getModel",
      description = "Searchers for and retrieve a model by manufacturer, name, and year",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The model received",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "timestamp": "2024-05-14T16:49:20.184738133",
                "errorCode": 404,
                "details": "The model with manufacturer 'Chevrolet', name 'Q9' and year '2025' not found"
              }
              """))),
        @ApiResponse(responseCode = "400", description = "The model modelYear must be positive"),
        @ApiResponse(responseCode = "404", description = "The model has not been found")
      })
  @GetMapping(value = V1 + MODEL_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ModelDto getModel(
      @PathVariable String manufacturer,
      @PathVariable String name,
      @PathVariable @Positive int year) {
    return modelService.getModel(manufacturer, name, year);
  }

  /**
   * Searches for models by provided parameters if no one is present it returns all models. There is
   * validation for year parameters, its value must be positive, and maxYear value must be after
   * minYear value.
   *
   * @param searchFilter - a parameter object containing maxYear, minYear, year, manufacturer, name,
   *     category parameters
   * @param pageRequest - an object with page parameters
   * @return - Page<ModelDto> - a page containing models
   */
  @Operation(
      summary = "Searchers models",
      operationId = "searchModels",
      description =
          "Searchers for models by optional parameters when no one is specified, retrieves all models",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The sorted page of models",
            useReturnTypeSchema = true),
        @ApiResponse(
            responseCode = "400",
            description = "The maxYear, minYear, modelYear parameters must be positive",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "timestamp": "2024-05-14T16:09:01.564992622",
                "errorCode": 400,
                "details": {
                  "minYear": "must be greater than 0"
                }
              }
            """))),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content")))
      })
  @GetMapping(value = V1 + MODELS_PATH)
  public Page<ModelDto> searchModels(
      @Valid @ParameterObject SearchFilter searchFilter, @ParameterObject Pageable pageRequest) {
    return modelService.searchModel(searchFilter, pageRequest);
  }

  @Operation(
      summary = "Get a model by its id",
      operationId = "getModelById",
      description = "Search for and retrieve a model from the database by its ID",
      tags = "model",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The model",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "id": "52096834-48af-41d1-b422-93600eff629a",
                "name": "A7",
                "modelYear": 2020,
                "manufacturer": "Audi",
                "categories": [
                  "Sedan"
                ]
              }
              """))),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content"))),
        @ApiResponse(
            responseCode = "404",
            description = "The model has not been found",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
            {
              "timestamp": "2024-05-13T19:41:22.788843813",
              "errorCode": 404,
              "details": "The model with id=37ce882b-f56b-4e66-a1a0-eab0f252bce6 not found"
            }
            """)))
      })
  @GetMapping(V1 + MODEL_ID_PATH)
  public ModelDto getModelById(@PathVariable UUID id) {
    return modelService.getModelById(id);
  }

  /**
   * Creates a model with provided parameters if no such one is present otherwise the exception will
   * be thrown.
   *
   * @param manufacturer - manufacturer name
   * @param name - model name
   * @param year - year
   * @param modelDto - a DTO containing model categories
   * @return ResponseEntity<Void>
   */
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
                "The model must have a non-empty category and a model modelYear must be positive",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
          {
            "timestamp": "2024-05-13T19:30:34.002022885",
            "errorCode": 400,
            "details": {
              "categories": "must not be null"
            }
          }
          """))),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content"))),
        @ApiResponse(
            responseCode = "409",
            description = "The model already exists",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
          {
            "timestamp": "2024-05-13T19:14:07.474768927",
            "errorCode": 409,
            "details": "The model with manufacturer 'Chevrolet', name 'Q9' and modelYear '20292' already exists, \
          its id='2482dbde-60c3-43cc-850e-8da8235a5510'"
          }
          """)))
      })
  @PostMapping(value = V1 + MODEL_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> createModel(
      @PathVariable String manufacturer,
      @PathVariable String name,
      @PathVariable @Positive Integer year,
      @RequestBody @Valid ModelDto modelDto) {
    modelDto.setManufacturer(manufacturer);
    modelDto.setName(name);
    modelDto.setYear(year);

    ModelDto persistedModel = modelService.createModel(modelDto);
    URI location =
        ServletUriComponentsBuilder.fromCurrentServletMapping()
            .path(V1 + MODEL_ID_PATH)
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
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
            {
              "timestamp": "2024-05-12T12:04:46.015013732",
              "errorCode": 400,
              "details": {
                "updateModel.modelYear": "must be greater than 0"
              }
            }
            """))),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content"))),
        @ApiResponse(
            responseCode = "404",
            description = "A model has not been found",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "timestamp": "2024-05-12T10:36:28.639097556",
                "errorCode": 404,
                "details": "The model with manufacturer 'Chevrolet', name 'Malibu' and modelYear '20292' not found"
              }
              """)))
      })
  @ResponseStatus(OK)
  @PutMapping(value = V1 + MODEL_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateModel(
      @Parameter(description = "A manufacturer name", example = "BMW") @PathVariable
          String manufacturer,
      @Parameter(description = "A model name", example = "x7") @PathVariable String name,
      @Parameter(description = "A modelYear of a model", example = "'2023") @PathVariable @Positive
          int year,
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
            responseCode = "401",
            description = "The user is not authorized",
            content = @Content(examples = @ExampleObject("no content"))),
        @ApiResponse(
            responseCode = "404",
            description = "The model not found",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "timestamp": "2024-05-12T10:20:47.550424959",
                "errorCode": 404,
                "details": "The model with id=d2541b4c-4882-439e-ab9c-f0c5a3924c47 not found"
              }
              """)))
      })
  public void deleteModelById(
      @Parameter(description = "a model ID", example = "52096834-48af-41d1-b422-93600eff629a")
          @PathVariable
          UUID id) {
    modelService.deleteModelById(id);
  }
}
