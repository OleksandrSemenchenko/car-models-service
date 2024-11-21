package ua.nicegear.cars.bot.controller.strategies.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategies.ResponseProcessor;
import ua.nicegear.cars.bot.controller.strategies.Strategy;

@RequiredArgsConstructor
public class MaxYearQueryStrategy extends ResponseProcessor implements Strategy {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;

  @Override
  public SendMessage execute(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    AnswerCallbackQuery answerCallbackQuery =
      AnswerCallbackQuery.builder()
        .callbackQueryId(update.getCallbackQuery().getId())
        .text(buttonsConfig.getPrompts().getMaxYear())
        .build();
    super.processResponse(telegramClient::execute, answerCallbackQuery);
    ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");
    sendMessage.setReplyMarkup(forceReplyKeyboard);
    sendMessage.setText(buttonsConfig.getPrompts().getMaxYear());
    return sendMessage;
  }
}
