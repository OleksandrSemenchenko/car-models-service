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

  public SearchDashboardStrategy(
      TelegramClient telegramClient, FilterService filterService, ButtonsConfig buttonsConfig) {
    super(telegramClient);
    this.filterService = filterService;
    this.buttonsConfig = buttonsConfig;
  }

  @Override
  public void execute(Update update) {
    long chatId = selectChatId(update);
    SendMessage sendMessage = makeSearchDashboardView(chatId);
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

  protected SendMessage makeSearchDashboardView(long chatId) {
    FilterDto searchFiltersDto = filterService.getFiltersByChatId(chatId);
    DashboardViewMaker searchDashboardViewMaker =
        new SearchDashboardViewMaker(buttonsConfig, searchFiltersDto);
    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("").build();
    return searchDashboardViewMaker.makeView(sendMessage);
  }
}
