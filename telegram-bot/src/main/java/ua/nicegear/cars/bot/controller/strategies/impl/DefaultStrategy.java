package ua.nicegear.cars.bot.controller.strategies.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategies.ResponseProcessor;
import ua.nicegear.cars.bot.controller.strategies.Strategy;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.service.SearchFilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.SearchDashboardViewMaker;

@RequiredArgsConstructor
public class DefaultStrategy extends ResponseProcessor implements Strategy {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final CommandsConfig commandsConfig;
  private final SearchFilterService searchFilterService;

  @Override
  public SendMessage execute(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");
    sendMessage = makeBaseDashboardView(sendMessage);
    addMenuButtonViewAndProcessResponse(chatId);

    String callbackMessage = update.getMessage().getText();

    if (callbackMessage.equals(CallbackMessage.STOP_COMMAND)) {
      // TODO
      sendMessage = SendMessage.builder().text("TODO").build();
    } else if (callbackMessage.equals(buttonsConfig.getNames().getSearchDashboard())) {

      chatId = update.getMessage().getChatId();
      sendMessage = makeSearchDashboardView(sendMessage, chatId);
    }
    return sendMessage;
  }

  private void addMenuButtonViewAndProcessResponse(long chatId) {
    MenuButtonViewMaker menuButtonViewMaker = new CommandsMenuButtonViewMaker(commandsConfig);
    MenuButtonView menuButtonView = menuButtonViewMaker.makeView(chatId);
    super.processResponse(telegramClient::execute, menuButtonView.getMyCommands());
    super.processResponse(telegramClient::execute, menuButtonView.getChatMenuButton());
  }

  private SendMessage makeBaseDashboardView(SendMessage sendMessage) {
    DashboardViewMaker dashboardViewMaker = new BaseDashboardViewMaker(buttonsConfig);
    return dashboardViewMaker.makeView(sendMessage);
  }

  private SendMessage makeSearchDashboardView(SendMessage sendMessage, long chatId) {
    SearchFilterDto searchFiltersDto = searchFilterService.getSearchFilterByChatId(chatId);
    DashboardViewMaker searchDashboardViewMaker =
      new SearchDashboardViewMaker(buttonsConfig, searchFiltersDto);
    return searchDashboardViewMaker.makeView(sendMessage);
  }
}
