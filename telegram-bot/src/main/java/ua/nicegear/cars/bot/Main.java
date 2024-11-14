package ua.nicegear.cars.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;

@SpringBootApplication
@EnableConfigurationProperties(ButtonNamesConfig.class)
@PropertySource("classpath:/application.yaml")
public class Main {

  public static void main(String[] ars) {
    SpringApplication.run(Main.class);
  }
}
