package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;

public class ForceReplyStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final String message;

  public ForceReplyStrategy(TelegramClient telegramClient, String message) {
    super(telegramClient);
    this.message = message;
  }

  @Override
  public void execute(Update update) {
    super.processAnswerCallbackQuery(update, message);
    super.processForceReplyKeyboard(update, message);
  }
}
