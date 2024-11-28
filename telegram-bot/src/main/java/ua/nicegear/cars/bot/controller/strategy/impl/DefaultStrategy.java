package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.service.FilterService;

public class DefaultStrategy extends AbstractStrategy implements ConsumeStrategy {

  private final CommandsConfig commandsConfig;
  private final FilterService filterService;
  private final ButtonsConfig buttonsConfig;

  public DefaultStrategy(
      TelegramClient telegramClient,
      FilterService filterService,
      ButtonsConfig buttonsConfig,
      CommandsConfig commandsConfig) {
    super(telegramClient);
    this.commandsConfig = commandsConfig;
    this.filterService = filterService;
    this.buttonsConfig = buttonsConfig;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Lets get started");
    MainDefaultStrategy mainDefaultStrategy =
        new MainDefaultStrategy(telegramClient, commandsConfig, buttonsConfig, sendMessage);
    this.add(mainDefaultStrategy);
    String callbackMessage = update.getMessage().getText();

    if (callbackMessage.equals(CallbackMessage.STOP_COMMAND)) {
      // TODO implement stop command
      sendMessage.setText("TODO");
    } else if (callbackMessage.equals(buttonsConfig.getNames().getSearchDashboard())) {
      AbstractStrategy searchDashboardStrategy =
          new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig, sendMessage);
      this.add(searchDashboardStrategy);
    }
    mainDefaultStrategy.execute(update);
  }
}
