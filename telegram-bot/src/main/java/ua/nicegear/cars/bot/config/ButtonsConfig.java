package ua.nicegear.cars.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot.buttons")
@Data
public class ButtonsConfig {

  private Names names;
  private Prompts prompts;

  @Data
  public static class Names {
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
    private String apply;
    private String hatchback;
    private String sedan;
    private String minivan;
    private String crossover;
  }

  @Data
  public static class Prompts {
    private String maxYear;
    private String minYear;
    private String maxMileage;
    private String numberOfOwners;
    private String bodyStyle;
    private String nullValue;
    private String applyAndSearch;
  }
}
