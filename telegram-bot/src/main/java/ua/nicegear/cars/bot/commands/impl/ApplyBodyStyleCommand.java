package ua.nicegear.cars.bot.commands.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.ButtonCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.AbstractStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.AnswerCallbackQueryStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.SearchDashboardStrategy;
import ua.nicegear.cars.bot.service.FilterService;

@RequiredArgsConstructor
public class ApplyBodyStyleCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  @Override
  public void setStrategyTo(Context context) {
    AnswerCallbackQueryStrategy answerCallbackQueryStrategy =
        new AnswerCallbackQueryStrategy(telegramClient, "");
    AbstractStrategy searchDashboardStrategy =
        new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig);
    answerCallbackQueryStrategy.add(searchDashboardStrategy);
    context.setConsumeStrategy(answerCallbackQueryStrategy);
  }
}
