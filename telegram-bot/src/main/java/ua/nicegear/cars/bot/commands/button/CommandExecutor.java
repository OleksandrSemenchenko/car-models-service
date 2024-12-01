package ua.nicegear.cars.bot.commands.button;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.service.FilterService;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {

  private final Context context;
  private Map<String, ButtonCommand> commandMap = new HashMap<>();

  public CommandExecutor(TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService, Context context) {
    this.context = context;
    commandMap.put(CallbackMessage.MIN_YEAR, new MinYearCommand(telegramClient, buttonsConfig));
    commandMap.put(CallbackMessage.MAX_YEAR, new MaxYearCommand(telegramClient, buttonsConfig));
    commandMap.put(CallbackMessage.MAX_MILEAGE, new MaxMileageCommand(telegramClient, buttonsConfig));
    commandMap.put(CallbackMessage.BODY_STYLE, new BodyStyleCommand(telegramClient, buttonsConfig, filterService));
  }

  public void execute(String key) {
    ButtonCommand buttonCommand = commandMap.get(key);
    buttonCommand.setStrategyTo(context);
  }
}
