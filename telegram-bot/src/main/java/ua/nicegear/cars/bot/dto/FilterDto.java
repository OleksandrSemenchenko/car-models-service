package ua.nicegear.cars.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

  Long userId;
  Integer maxYear;
  Integer minYear;
  Integer maxMileage;
  Integer numberOfOwners;
  BodyStyle bodyStyle;
}
