package ua.foxminded.cars.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@RestController
@RequestMapping("/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

  private final ManufacturerService manufacturerService;

  @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ManufacturerDto> getManufacturer(@PathVariable String name) {
    ManufacturerDto manufacturerDto = manufacturerService.getManufacturer(name);
    return ResponseEntity.ok(manufacturerDto);
  }
}
