package ua.nicegear.cars.bot.controller.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ResponseProcessor;
import ua.nicegear.cars.bot.controller.strategy.Strategy;

@RequiredArgsConstructor
public class MaxYearQueryStrategy extends ResponseProcessor implements Strategy {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;

  @Override
  public void execute(Update update) {
    String message = buttonsConfig.getPrompts().getMaxYear();
    processAnswerCallbackQuery(update, message);
    processForceReplyKeyboard(update, message);
  }

  private void processAnswerCallbackQuery(Update update, String message) {
    AnswerCallbackQuery answerCallbackQuery =
        AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text(message)
            .build();
    super.processResponse(telegramClient::execute, answerCallbackQuery);
  }

  private void processForceReplyKeyboard(Update update, String message) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .replyMarkup(forceReplyKeyboard)
            .text(message)
            .build();
    super.processResponse(telegramClient::execute, sendMessage);
  }
}
