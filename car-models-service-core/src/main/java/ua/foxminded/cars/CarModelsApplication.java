package ua.foxminded.cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ua.foxminded.cars.config.AppConfig;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@EnableConfigurationProperties(AppConfig.class)
@PropertySource("classpath:/application.yml")
public class CarModelsApplication {

  public static void main(String[] args) {
    SpringApplication.run(CarModelsApplication.class, args);
  }
}
