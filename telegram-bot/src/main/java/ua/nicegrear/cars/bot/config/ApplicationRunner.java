package ua.nicegrear.cars.bot.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.nicegrear.cars.bot.controller.BotController;

@Component
public class ApplicationRunner extends TelegramBotsLongPollingApplication {

  public ApplicationRunner(@Value("${bot.token}") String token) throws TelegramApiException {
    this.registerBot(token, new BotController(token));
  }

  @PreDestroy
  public void close() throws Exception {
    super.close();
  }
}
