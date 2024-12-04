package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.CommandExecutor;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.service.FilterService;

public class CallbackQueryStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  public CallbackQueryStrategy(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
    this.filterService = filterService;
  }

  @Override
  public void execute(Update update) {
    String command = update.getCallbackQuery().getData();
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    Context context = new Context();
    CommandExecutor commandExecutor =
        new CommandExecutor(super.telegramClient, buttonsConfig, filterService, context, chatId);
    commandExecutor.execute(command);
    context.executeStrategy(update);
  }
}
