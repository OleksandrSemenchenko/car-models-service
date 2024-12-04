package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;

public class MainDefaultStrategy extends AbstractStrategy implements ConsumeStrategy {

  private final CommandsConfig commandsConfig;
  private final ButtonsConfig buttonsConfig;
  private SendMessage sendMessage;

  public MainDefaultStrategy(
      TelegramClient telegramClient,
      CommandsConfig commandsConfig,
      ButtonsConfig buttonsConfig,
      SendMessage sendMessage) {
    super(telegramClient);
    this.commandsConfig = commandsConfig;
    this.buttonsConfig = buttonsConfig;
    this.sendMessage = sendMessage;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getMessage().getChatId();
    sendMessage = makeBaseDashboardView(sendMessage);
    addMenuButtonViewAndProcessResponse(chatId);
    checkNext(sendMessage, update);
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
}
