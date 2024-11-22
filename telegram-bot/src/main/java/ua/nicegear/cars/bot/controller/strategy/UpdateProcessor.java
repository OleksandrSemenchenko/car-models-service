package ua.nicegear.cars.bot.controller.strategy;

import io.micrometer.observation.Observation;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
public abstract class UpdateProcessor {

  protected final TelegramClient telegramClient;

  protected void processAnswerCallbackQuery(Update update, String message) {
    AnswerCallbackQuery answerCallbackQuery =
        AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text(message)
            .build();
    processUpdate(telegramClient::execute, answerCallbackQuery);
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
    processUpdate(telegramClient::execute, sendMessage);
  }

  protected <T, R, E extends TelegramApiException> R processUpdate(
      Observation.CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
