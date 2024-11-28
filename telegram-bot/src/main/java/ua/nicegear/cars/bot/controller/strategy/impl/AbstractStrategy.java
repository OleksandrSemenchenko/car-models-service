package ua.nicegear.cars.bot.controller.strategy.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;

public abstract class AbstractStrategy extends UpdateProcessor implements ConsumeStrategy {

  private Deque<AbstractStrategy> strategies;
  private AbstractStrategy nextStrategy;

  public AbstractStrategy(TelegramClient telegramClient) {
    super(telegramClient);
    this.strategies = new ArrayDeque<>();
    strategies.add(this);
  }

  public void add(AbstractStrategy strategy) {
    strategies.getLast().setNextStrategy(strategy);
    strategies.add(strategy);
  }

  private void setNextStrategy(AbstractStrategy consumeStrategy) {
    this.nextStrategy = consumeStrategy;
  }

  protected void checkNext(SendMessage sendMessage, Update update) {
    if (Objects.isNull(nextStrategy)) {
      processResponse(telegramClient::execute, sendMessage);
    } else {
      nextStrategy.execute(update);
    }
  }
}
