package ua.nicegear.cars.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot.button.names")
@Data
public class ButtonNamesConfig {

  private String maxYear;
  private String minYear;
  private String maxMileage;
  private String numberOfOwners;
  private String bodyStyle;
  private String details;
  private String delete;
  private String searchDashboard;
  private String applyAndSearch;
  private String close;
}
