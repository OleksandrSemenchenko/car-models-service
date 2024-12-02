package ua.nicegear.cars.bot.commands;

import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.impl.ApplyBodyStyleCommand;
import ua.nicegear.cars.bot.commands.impl.BodyStyleCommand;
import ua.nicegear.cars.bot.commands.impl.BodyStyleDeleteCommand;
import ua.nicegear.cars.bot.commands.impl.CrossoverCommand;
import ua.nicegear.cars.bot.commands.impl.HatchbackCommand;
import ua.nicegear.cars.bot.commands.impl.MaxMileageCommand;
import ua.nicegear.cars.bot.commands.impl.MaxMileageDeleteCommand;
import ua.nicegear.cars.bot.commands.impl.MaxYearCommand;
import ua.nicegear.cars.bot.commands.impl.MaxYearDeleteCommand;
import ua.nicegear.cars.bot.commands.impl.MinYearCommand;
import ua.nicegear.cars.bot.commands.impl.MinYearDeleteCommand;
import ua.nicegear.cars.bot.commands.impl.MinivanCommand;
import ua.nicegear.cars.bot.commands.impl.NumberOfOwnersCommand;
import ua.nicegear.cars.bot.commands.impl.NumberOfOwnersDeleteCommand;
import ua.nicegear.cars.bot.commands.impl.SedanCommand;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.service.FilterService;

public class CommandExecutor {

  private final Context context;
  private Map<String, ButtonCommand> commandMap = new HashMap<>();

  public CommandExecutor(
      TelegramClient telegramClient,
      ButtonsConfig buttonsConfig,
      FilterService filterService,
      Context context,
      long chatId) {
    this.context = context;
    commandMap.put(CallbackMessage.MIN_YEAR, new MinYearCommand(telegramClient, buttonsConfig));
    commandMap.put(CallbackMessage.MAX_YEAR, new MaxYearCommand(telegramClient, buttonsConfig));
    commandMap.put(
        CallbackMessage.MAX_MILEAGE, new MaxMileageCommand(telegramClient, buttonsConfig));
    commandMap.put(
        CallbackMessage.BODY_STYLE,
        new BodyStyleCommand(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.HATCHBACK,
        new HatchbackCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.SEDAN,
        new SedanCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.CROSSOVER,
        new CrossoverCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.MINIVAN,
        new MinivanCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.APPLY_BODY_STYLE,
        new ApplyBodyStyleCommand(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.MAX_YEAR_DELETE,
        new MaxYearDeleteCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.MIN_YEAR_DELETE,
        new MinYearDeleteCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.MAX_MILLAGE_DELETE,
        new MaxMileageDeleteCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.NUMBER_OF_OWNERS, new NumberOfOwnersCommand(telegramClient, buttonsConfig));
    commandMap.put(
        CallbackMessage.NUMBER_OF_OWNERS_DELETE,
        new NumberOfOwnersDeleteCommand(telegramClient, buttonsConfig, filterService, chatId));
    commandMap.put(
        CallbackMessage.BODY_STYLE_DELETE,
        new BodyStyleDeleteCommand(telegramClient, buttonsConfig, filterService, chatId));
  }

  public void execute(String key) {
    ButtonCommand buttonCommand = commandMap.get(key);
    buttonCommand.setStrategyTo(context);
  }
}
