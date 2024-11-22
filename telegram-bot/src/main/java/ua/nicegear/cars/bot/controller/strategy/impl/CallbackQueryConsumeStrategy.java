package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.service.FilterService;

public class CallbackQueryConsumeStrategy extends UpdateProcessor implements ConsumeStrategy {

  public CallbackQueryConsumeStrategy(
      TelegramClient telegramClient, FilterService filterService, ButtonsConfig buttonsConfig) {
    super(telegramClient, filterService, buttonsConfig);
  }

  @Override
  public void execute(Update update) {
    String callbackMessage = update.getCallbackQuery().getData();
    String message = "";

    if (callbackMessage.equals(buttonsConfig.getNames().getMaxYear())) {
      message = buttonsConfig.getPrompts().getMaxYear();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMinYear())) {
      message = buttonsConfig.getPrompts().getMinYear();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMaxMileage())) {
      message = buttonsConfig.getPrompts().getMaxMileage();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getNumberOfOwners())) {
      message = buttonsConfig.getPrompts().getNumberOfOwners();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getBodyStyle())) {
      message = buttonsConfig.getPrompts().getBodyStyle();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getApplyAndSearch())) {
      // TODO ApplyAndSearch actions

    } else if (callbackMessage.equals(buttonsConfig.getNames().getClose())) {
      // TODO Close actions

    }
    super.processAnswerCallbackQuery(update, message);
    super.processForceReplyKeyboard(update, message);
  }
}
