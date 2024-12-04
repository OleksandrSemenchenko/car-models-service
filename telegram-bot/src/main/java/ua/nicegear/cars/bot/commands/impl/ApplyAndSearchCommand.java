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
public class ApplyAndSearchCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;
  private final long chatId;

  @Override
  public void setStrategyTo(Context context) {
    filterService.persistCache(chatId);
    AbstractStrategy answerCallbackQueryStrategy =
        new AnswerCallbackQueryStrategy(
            telegramClient, buttonsConfig.getPrompts().getApplyAndSearch());
    AbstractStrategy searchDashboardStrategy =
        new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig);
    answerCallbackQueryStrategy.add(searchDashboardStrategy);
    context.setConsumeStrategy(answerCallbackQueryStrategy);

    // TODO the strategy to print cars here is needed

  }
}
