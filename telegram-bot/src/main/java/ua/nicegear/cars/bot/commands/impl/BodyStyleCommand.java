package ua.nicegear.cars.bot.commands.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.ButtonCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.BodyStyleStrategy;
import ua.nicegear.cars.bot.service.FilterService;

@RequiredArgsConstructor
public class BodyStyleCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  @Override
  public void setStrategyTo(Context context) {
    ConsumeStrategy bodyStyleStrategy =
        new BodyStyleStrategy(telegramClient, buttonsConfig, filterService);
    context.setConsumeStrategy(bodyStyleStrategy);
  }
}
