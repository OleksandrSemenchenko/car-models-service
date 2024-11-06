package ua.nicegrear.cars.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Objects;

@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;

  @Value("${bot.username}")
  private String username;

  public BotController(String token) {
    this.telegramClient = new OkHttpTelegramClient(token);
  }

  @Override
  public void consume(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String receivedMessage = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      SendMessage message = SendMessage.builder()
        .chatId(chatId)
        .text(receivedMessage)
        .build();

      executeSendMessage(message);
    }
  }

  private void executeSendMessage(SendMessage message) {
    if (Objects.nonNull(message)) {
      try {
        telegramClient.execute(message);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
