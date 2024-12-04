package ua.nicegear.cars.bot.controller.strategy;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
public class Context {

  private ConsumeStrategy consumeStrategy;

  public void executeStrategy(Update update) {
    consumeStrategy.execute(update);
  }
}
