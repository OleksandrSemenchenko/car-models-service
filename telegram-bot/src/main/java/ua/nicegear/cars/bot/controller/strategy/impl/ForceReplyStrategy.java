package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;

public class ForceReplyStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final String message;

  public ForceReplyStrategy(TelegramClient telegramClient, String message) {
    super(telegramClient);
    this.message = message;
  }

  @Override
  public void execute(Update update) {
    String callbackMessage = update.getCallbackQuery().getData();
    processAnswerCallbackQuery(update, message);
    processForceReplyKeyboard(update, message);
  }

  protected void processAnswerCallbackQuery(Update update, String message) {
    AnswerCallbackQuery answerCallbackQuery =
        AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text(message)
            .build();
    processResponse(telegramClient::execute, answerCallbackQuery);
  }

  protected void processForceReplyKeyboard(Update update, String message) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .replyMarkup(forceReplyKeyboard)
            .text(message)
            .build();
    processResponse(telegramClient::execute, sendMessage);
  }
}
