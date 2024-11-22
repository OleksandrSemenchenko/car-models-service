package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.service.SearchFilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.SearchDashboardViewMaker;

public class DefaultConsumeStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final CommandsConfig commandsConfig;
  private final SearchFilterService searchFilterService;

  public DefaultConsumeStrategy(
      TelegramClient telegramClient,
      ButtonsConfig buttonsConfig,
      CommandsConfig commandsConfig,
      SearchFilterService searchFilterService) {
    super(telegramClient);
    this.telegramClient = telegramClient;
    this.buttonsConfig = buttonsConfig;
    this.commandsConfig = commandsConfig;
    this.searchFilterService = searchFilterService;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Echo");
    sendMessage = makeBaseDashboardView(sendMessage);
    addMenuButtonViewAndProcessResponse(chatId);
    String callbackMessage = update.getMessage().getText();

    if (callbackMessage.equals(CallbackMessage.STOP_COMMAND)) {
      // TODO
      sendMessage.setText("TODO");
    } else if (callbackMessage.equals(buttonsConfig.getNames().getSearchDashboard())) {

      chatId = update.getMessage().getChatId();
      sendMessage = makeSearchDashboardView(sendMessage, chatId);
    }
    processUpdate(telegramClient::execute, sendMessage);
  }

  private void addMenuButtonViewAndProcessResponse(long chatId) {
    MenuButtonViewMaker menuButtonViewMaker = new CommandsMenuButtonViewMaker(commandsConfig);
    MenuButtonView menuButtonView = menuButtonViewMaker.makeView(chatId);
    super.processUpdate(telegramClient::execute, menuButtonView.getMyCommands());
    super.processUpdate(telegramClient::execute, menuButtonView.getChatMenuButton());
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
