package ua.nicegear.cars.bot.controller.strategy.impl;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.SearchDashboardViewMaker;

public class SearchDashboardStrategy extends AbstractStrategy implements ConsumeStrategy {

  private final FilterService filterService;
  private final ButtonsConfig buttonsConfig;
  private SendMessage sendMessage;

  public SearchDashboardStrategy(
      TelegramClient telegramClient,
      FilterService filterService,
      ButtonsConfig buttonsConfig,
      SendMessage sendMessage) {
    super(telegramClient);
    this.filterService = filterService;
    this.buttonsConfig = buttonsConfig;
    this.sendMessage = sendMessage;
  }

  @Override
  public void execute(Update update) {
    long chatId = selectChatId(update);
    sendMessage = makeSearchDashboardView(sendMessage, chatId);
    checkNext(sendMessage, update);
  }

  private long selectChatId(Update update) {
    if (Objects.nonNull(update.getMessage())) {
      return update.getMessage().getChatId();
    }
    if (Objects.nonNull(update.getCallbackQuery())) {
      return update.getCallbackQuery().getMessage().getChatId();
    }
    return 0L;
  }

  protected SendMessage makeSearchDashboardView(SendMessage sendMessage, long chatId) {
    FilterDto searchFiltersDto = filterService.getSearchFilterByChatId(chatId);
    DashboardViewMaker searchDashboardViewMaker =
        new SearchDashboardViewMaker(buttonsConfig, searchFiltersDto);
    return searchDashboardViewMaker.makeView(sendMessage);
  }
}
