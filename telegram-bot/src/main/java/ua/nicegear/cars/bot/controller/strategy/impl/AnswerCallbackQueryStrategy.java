package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class AnswerCallbackQueryStrategy extends AbstractStrategy {

  private final String message;

  public AnswerCallbackQueryStrategy(TelegramClient telegramClient, String message) {
    super(telegramClient);
    this.message = message;
  }

  @Override
  public void execute(Update update) {
    super.processAnswerCallbackQuery(update, message);
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    SendMessage sendMessage = SendMessage.builder().text(message).chatId(chatId).build();
    checkNext(sendMessage, update);
  }
}
