package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;

public class DefaultConsumeStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final CommandsConfig commandsConfig;

  public DefaultConsumeStrategy(
      TelegramClient telegramClient,
      FilterService filterService,
      ButtonsConfig buttonsConfig,
      CommandsConfig commandsConfig) {
    super(telegramClient, filterService, buttonsConfig);
    this.commandsConfig = commandsConfig;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getMessage().getChatId();
    // TODO
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Echo");
    sendMessage = makeBaseDashboardView(sendMessage);
    addMenuButtonViewAndProcessResponse(chatId);
    String callbackMessage = update.getMessage().getText();

    if (callbackMessage.equals(CallbackMessage.STOP_COMMAND)) {
      // TODO
      sendMessage.setText("TODO");
    } else if (callbackMessage.equals(super.buttonsConfig.getNames().getSearchDashboard())) {
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
    DashboardViewMaker dashboardViewMaker = new BaseDashboardViewMaker(super.buttonsConfig);
    return dashboardViewMaker.makeView(sendMessage);
  }
}
