package ua.nicegrear.cars.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.nicegrear.cars.bot.controller.BotController;

@Configuration
public class BotConfig {

  @Value("${bot.token}")
  private String token;

  @Bean
  TelegramBotsLongPollingApplication telegramBotApi() throws TelegramApiException {
    TelegramBotsLongPollingApplication pollingApplication = new TelegramBotsLongPollingApplication();
    pollingApplication.registerBot(token, new BotController(token));
    return pollingApplication;
  }
}
