package ua.nicegear.cars.bot.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.nicegear.cars.bot.controller.BotController;

@Component
public class ApplicationRunner extends TelegramBotsLongPollingApplication {

  public ApplicationRunner(@Value("${bot.token}") String token, BotController botController)
      throws TelegramApiException {
    this.registerBot(token, botController);
  }

  @PreDestroy
  public void close() throws Exception {
    super.close();
  }
}
