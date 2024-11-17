package ua.nicegear.cars.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot.commands")
@Data
public class CommandNamesConfig {

  private String stop;
}
