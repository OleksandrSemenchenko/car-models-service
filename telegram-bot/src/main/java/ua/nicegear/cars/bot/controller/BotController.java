package ua.nicegear.cars.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.CallbackQueryConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.DefaultConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.ReplyConsumeStrategy;
import ua.nicegear.cars.bot.service.FilterService;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final FilterService filterService;
  private final ButtonsConfig buttonsConfig;
  private final CommandsConfig commandsConfig;

  @Override
  public void consume(Update update) {
    Context context = new Context();

    if (update.hasCallbackQuery()) {
      context.setConsumeStrategy(
          new CallbackQueryConsumeStrategy(telegramClient, filterService, buttonsConfig));
    } else if (update.getMessage().isReply()) {
      context.setConsumeStrategy(
          new ReplyConsumeStrategy(telegramClient, filterService, buttonsConfig));
    } else {
      context.setConsumeStrategy(
          new DefaultConsumeStrategy(telegramClient, filterService, buttonsConfig, commandsConfig));
    }
    context.executeStrategy(update);
  }
}
