package ua.nicegear.cars.bot.controller.strategy;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
public class Context {

  private Strategy strategy;

  public void executeStrategy(Update update) {
    strategy.execute(update);
  }
}
