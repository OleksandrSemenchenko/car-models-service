package ua.nicegrear.cars.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import ua.nicegrear.cars.bot.controller.BotController;

@Configuration
public class ApplicationRunnerConfig {

  @Value("${bot.token}")
  private String token;

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      try (TelegramBotsLongPollingApplication botsApplication =
             new TelegramBotsLongPollingApplication();) {
        botsApplication.registerBot(token, new BotController(token));
        Thread.currentThread().join();
      }
    };
  }
}
