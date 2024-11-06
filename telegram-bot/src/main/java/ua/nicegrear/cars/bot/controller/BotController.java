package ua.nicegrear.cars.bot.controller;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;

  public BotController(String token) {
    this.telegramClient = new OkHttpTelegramClient(token);
  }

  @Override
  public void consume(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String receivedMessage = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      SendMessage sendMessage = selectAction(receivedMessage, chatId);
      addMenuTo(sendMessage);
      executeSendMessage(sendMessage);
    }
  }

  private SendMessage selectAction(String receivedMessage, long charId) {
    if (receivedMessage.equals("Row1Button1")) {
      return getAllCars(charId);
    } else {
      return doNothing(charId);
    }
  }

  private SendMessage getAllCars(long chatId) {
    return SendMessage.builder().text("There are all cars").chatId(chatId).build();
  }

  private SendMessage doNothing(long chatId) {
    return SendMessage.builder().chatId(chatId).text("No actions").build();
  }

  private void addMenuTo(SendMessage message) {
    Objects.requireNonNull(message);

    KeyboardButton button1 = KeyboardButton.builder().text("Row1Button1").build();
    KeyboardButton button2 = KeyboardButton.builder().text("Row1Button2").build();
    KeyboardButton button3 = KeyboardButton.builder().text("Row2Button1").build();
    KeyboardButton button4 = KeyboardButton.builder().text("Row2Button2").build();

    KeyboardRow row1 = new KeyboardRow(button1, button2);
    KeyboardRow row2 = new KeyboardRow(button3, button4);

    ReplyKeyboardMarkup keyboardMarkup =
        ReplyKeyboardMarkup.builder().keyboard(List.of(row1, row2)).build();

    message.setReplyMarkup(keyboardMarkup);
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
