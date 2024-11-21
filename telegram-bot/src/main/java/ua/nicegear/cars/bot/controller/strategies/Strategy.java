package ua.nicegear.cars.bot.controller.strategies;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Strategy {

  public SendMessage execute(Update update);
}
