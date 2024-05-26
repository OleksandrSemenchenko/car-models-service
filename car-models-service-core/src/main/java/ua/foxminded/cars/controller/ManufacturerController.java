package ua.foxminded.cars.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@Tag(name = "ManufacturerController", description = "Manages manufacturers")
@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

  private final ManufacturerService manufacturerService;

  @Operation(
      summary = "Gets all manufacturers",
      operationId = "getAllManufacturers",
      description = "Gets all manufacturers on a sorted page",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "A page with manufacturers",
            useReturnTypeSchema = true),
        @ApiResponse(
            responseCode = "401",
            description = "A user not authorized",
            content = @Content(examples = @ExampleObject("no content")))
      })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<ManufacturerDto>> getAllManufacturers(Pageable pageable) {
    Page<ManufacturerDto> manufacturersPage = manufacturerService.getAllManufacturers(pageable);
    return ResponseEntity.ok(manufacturersPage);
  }

  @Operation(
      summary = "Gets a manufacturer",
      operationId = "getManufacturer",
      description = "Gets a manufacturer by the manufacturer name",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "A manufacturer",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
          {
            "name": "Audi"
          }
          """))),
        @ApiResponse(
            responseCode = "401",
            description = "A user not authorized",
            content = @Content(examples = @ExampleObject("no content"))),
        @ApiResponse(
            responseCode = "404",
            description = "A manufacturer not found",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            """
              {
                "timestamp": "2024-05-26T19:43:09.23112967",
                "errorCode": 404,
                "details": "The Lexus manufacturer not found"
              }
              """)))
      })
  @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ManufacturerDto> getManufacturer(@PathVariable String name) {
    ManufacturerDto manufacturerDto = manufacturerService.getManufacturer(name);
    return ResponseEntity.ok(manufacturerDto);
  }
}
