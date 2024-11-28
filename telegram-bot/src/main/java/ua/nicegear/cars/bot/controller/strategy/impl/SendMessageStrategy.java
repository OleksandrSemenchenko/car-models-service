package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SendMessageStrategy extends AbstractStrategy {

  private SendMessage sendMessage;

  public SendMessageStrategy(TelegramClient telegramClient, SendMessage sendMessage) {
    super(telegramClient);
    this.sendMessage = sendMessage;
  }

  @Override
  public void execute(Update update) {
    checkNext(sendMessage, update);
  }
}
