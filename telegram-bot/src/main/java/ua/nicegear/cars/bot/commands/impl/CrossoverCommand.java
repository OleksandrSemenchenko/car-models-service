package ua.nicegear.cars.bot.commands.impl;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.AbstractCommand;
import ua.nicegear.cars.bot.commands.ButtonCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.BodyStyleStrategy;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

public class CrossoverCommand extends AbstractCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  public CrossoverCommand(
      TelegramClient telegramClient,
      ButtonsConfig buttonsConfig,
      FilterService filterService,
      long chatId) {
    super(filterService, chatId);
    this.telegramClient = telegramClient;
    this.buttonsConfig = buttonsConfig;
    this.filterService = filterService;
  }

  @Override
  public void setStrategyTo(Context context) {
    super.updateCache(BodyStyle.CROSSOVER);
    context.setConsumeStrategy(new BodyStyleStrategy(telegramClient, buttonsConfig, filterService));
  }
}
