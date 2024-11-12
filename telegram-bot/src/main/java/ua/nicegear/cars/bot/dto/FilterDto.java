package ua.nicegear.cars.bot.dto;

import lombok.Data;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Data
public class FilterDto {

  Integer maxYear;
  Integer minYear;
  Integer maxMileage;
  Integer numberOfOwners;
  BodyStyle bodyStyle;
}
