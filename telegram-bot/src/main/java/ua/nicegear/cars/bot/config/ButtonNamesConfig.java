package ua.nicegear.cars.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot.button.names")
@Data
public class ButtonNamesConfig {

  String maxYear;
  String minYear;
  String maxMileage;
  String numberOfOwners;
  String bodyStyle;
  String details;
  String delete;
  String showFilters;
  String stop;
}
