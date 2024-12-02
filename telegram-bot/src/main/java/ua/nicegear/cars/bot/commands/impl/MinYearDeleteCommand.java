package ua.nicegear.cars.bot.commands.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.ButtonCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.AbstractStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.AnswerCallbackQueryStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.SearchDashboardStrategy;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;

@RequiredArgsConstructor
public class MinYearDeleteCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;
  private final long chartId;

  @Override
  public void setStrategyTo(Context context) {
    AbstractStrategy answerCallbackQueryStrategy =
        new AnswerCallbackQueryStrategy(telegramClient, "");
    FilterDto filterDto = filterService.getFiltersByChatId(chartId);
    filterDto.setMinYear(null);
    filterService.saveToCache(filterDto);
    AbstractStrategy searchDashboardStrategy =
        new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig);
    answerCallbackQueryStrategy.add(searchDashboardStrategy);
    context.setConsumeStrategy(answerCallbackQueryStrategy);
  }
}
