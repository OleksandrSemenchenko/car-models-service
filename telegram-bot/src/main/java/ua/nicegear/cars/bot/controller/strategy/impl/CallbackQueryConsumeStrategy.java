package ua.nicegear.cars.bot.controller.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;

@RequiredArgsConstructor
public class CallbackQueryConsumeStrategy implements ConsumeStrategy {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;

  @Override
  public void execute(Update update) {
    Context context = new Context();
    String callbackData = update.getCallbackQuery().getData();

    if (callbackData.equals(buttonsConfig.getNames().getMaxYear())) {
      context.setConsumeStrategy(new MaxYearConsumeStrategy(telegramClient, buttonsConfig));
    } else if (callbackData.equals(buttonsConfig.getNames().getMinYear())) {
      // TODO
    }
    context.executeStrategy(update);
  }
}
