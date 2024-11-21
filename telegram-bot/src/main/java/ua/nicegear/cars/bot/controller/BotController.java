package ua.nicegear.cars.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.DefaultStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.MaxYearQueryStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.ReplyStrategy;
import ua.nicegear.cars.bot.service.SearchFilterService;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final SearchFilterService searchFilterService;
  private final ButtonsConfig buttonsConfig;
  private final CommandsConfig commandsConfig;

  @Override
  public void consume(Update update) {
    Context context = new Context();

    if (update.hasCallbackQuery()) {
      String callbackData = update.getCallbackQuery().getData();

      if (callbackData.equals(buttonsConfig.getNames().getMaxYear())) {
        context.setStrategy(new MaxYearQueryStrategy(telegramClient, buttonsConfig));
      } else if (callbackData.equals(buttonsConfig.getNames().getMinYear())) {
        // TODO
      }
    } else if (update.getMessage().isReply()) {
      context.setStrategy(new ReplyStrategy(telegramClient));
    } else {
      context.setStrategy(
          new DefaultStrategy(telegramClient, buttonsConfig, commandsConfig, searchFilterService));
    }
    context.executeStrategy(update);
  }
}
