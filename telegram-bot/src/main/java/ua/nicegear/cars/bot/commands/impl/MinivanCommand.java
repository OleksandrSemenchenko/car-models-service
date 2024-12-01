package ua.nicegear.cars.bot.commands.impl;

import java.util.Set;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.AbstractCommand;
import ua.nicegear.cars.bot.commands.ButtonCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

public class MinivanCommand extends AbstractCommand implements ButtonCommand {

  public MinivanCommand(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient, buttonsConfig, filterService);
  }

  @Override
  public void setStrategyTo(Context context) {
    Set<BodyStyle> bodyStyles = Set.of(BodyStyle.MINIVAN);
    ConsumeStrategy strategy = super.getBodyStyleStrategy(bodyStyles);
    context.setConsumeStrategy(strategy);
  }
}
