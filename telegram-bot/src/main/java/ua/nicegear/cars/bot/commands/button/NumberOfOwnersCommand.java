package ua.nicegear.cars.bot.commands.button;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.ForceReplyStrategy;

@RequiredArgsConstructor
public class NumberOfOwnersCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;

  @Override
  public void setStrategyTo(Context context) {
    String message = buttonsConfig.getPrompts().getNumberOfOwners();
    context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
  }
}
