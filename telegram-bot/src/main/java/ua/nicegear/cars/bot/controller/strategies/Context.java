package ua.nicegear.cars.bot.controller.strategies;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
public class Context {

  private Strategy strategy;

  public SendMessage executeStrategy(Update update) {
    return strategy.execute(update);
  }
}
