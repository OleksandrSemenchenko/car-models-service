package ua.nicegear.cars.bot.commands;

import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.impl.ApplyBodyStyleCommand;
import ua.nicegear.cars.bot.commands.impl.BodyStyleCommand;
import ua.nicegear.cars.bot.commands.impl.Crossover;
import ua.nicegear.cars.bot.commands.impl.HatchbackCommand;
import ua.nicegear.cars.bot.commands.impl.MaxMileageCommand;
import ua.nicegear.cars.bot.commands.impl.MaxYearCommand;
import ua.nicegear.cars.bot.commands.impl.MinYearCommand;
import ua.nicegear.cars.bot.commands.impl.MinivanCommand;
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
      Context context) {
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
        new HatchbackCommand(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.SEDAN, new SedanCommand(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.CROSSOVER, new Crossover(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.MINIVAN, new MinivanCommand(telegramClient, buttonsConfig, filterService));
    commandMap.put(
        CallbackMessage.APPLY_BODY_STYLE,
        new ApplyBodyStyleCommand(telegramClient, buttonsConfig, filterService));
  }

  public void execute(String key) {
    ButtonCommand buttonCommand = commandMap.get(key);
    buttonCommand.setStrategyTo(context);
  }
}
