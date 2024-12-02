package ua.nicegear.cars.bot.dto;

import java.util.Set;
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

  Long chatId;
  Integer maxYear;
  Integer minYear;
  Integer maxMileage;
  Integer numberOfOwners;
  Set<BodyStyle> bodyStyles;
}
