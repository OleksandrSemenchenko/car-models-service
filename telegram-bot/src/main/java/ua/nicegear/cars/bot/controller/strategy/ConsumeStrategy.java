package ua.nicegear.cars.bot.controller.strategy;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumeStrategy {

  void execute(Update update);
}
