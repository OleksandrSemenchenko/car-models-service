package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.service.FilterService;

public class CallbackQueryStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final ButtonsConfig buttonsConfig;

  public CallbackQueryStrategy(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
  }

  @Override
  public void execute(Update update) {
    String callbackMessage = update.getCallbackQuery().getData();
    String message = "";
    Context context = new Context();

    if (callbackMessage.equals(buttonsConfig.getNames().getMaxYear())) {
      message = buttonsConfig.getPrompts().getMaxYear();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMinYear())) {
      message = buttonsConfig.getPrompts().getMinYear();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMaxMileage())) {
      message = buttonsConfig.getPrompts().getMaxMileage();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getNumberOfOwners())) {
      message = buttonsConfig.getPrompts().getNumberOfOwners();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getBodyStyle())) {
      ConsumeStrategy bodyStyleStrategy = new BodyStyleStrategy(telegramClient, buttonsConfig);
      context.setConsumeStrategy(bodyStyleStrategy);
    } else if (callbackMessage.equals(buttonsConfig.getNames().getApplyAndSearch())) {
      // TODO ApplyAndSearch actions

    }
    context.executeStrategy(update);
  }
}
