package ua.nicegear.cars.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ua.nicegear.cars.bot.enums.BodyStyle;

@ConfigurationProperties(prefix = "button.name")
@Data
public class ButtonNamesConfig {

  Integer maxYear;
  Integer minYear;
  Integer maxMileage;
  Integer numberOfOwners;
  BodyStyle bodyStyle;
  String details;
  String delete;
}
