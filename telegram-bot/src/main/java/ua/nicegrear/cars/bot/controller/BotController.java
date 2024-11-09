package ua.nicegrear.cars.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegrear.cars.bot.dto.ModelDto;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final ObjectMapper objectMapper;

  @Override
  public void consume(Update update) {
    SendMessage sendMessage;

    if (update.hasMessage() && update.getMessage().hasText()) {
      String receivedMessage = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      sendMessage = selectAction(receivedMessage, chatId);

      if (Objects.isNull(sendMessage.getReplyMarkup())) {
        addMenuTo(sendMessage);
      } else if (update.hasCallbackQuery()) {
        EditMessageText editMessageText = updateInlineButton(update);

        try {
          telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }
      }
      sendResponse(sendMessage);
    } else if (update.hasCallbackQuery()) {
      EditMessageText editMessageText = updateInlineButton(update);
      sendResponse(editMessageText);
    }
  }

  private EditMessageText updateInlineButton(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    long messageId = update.getCallbackQuery().getMessage().getMessageId();
    return EditMessageText.builder()
      .chatId(chatId)
      .messageId((int) messageId)
      .text("Updated inline text")
      .build();
  }

  private SendMessage selectAction(String receivedMessage, long charId) {
    if (receivedMessage.equals("Row1Button1")) {
      return getAllCars(charId);
    } else if (receivedMessage.equals("all cars")) {
      return doUnderAllCars(charId);
    } else {
      return doNothing(charId);
    }
  }

  private SendMessage getAllCars(long chatId) {
    ModelDto modelDto = ModelDto.builder()
      .name("A7")
      .manufacturer("Audi")
      .year(2013)
      .build();

    var button = InlineKeyboardButton.builder()
      .text("Detailed information")
      .callbackData("Detailed information")
      .build();
    var row = new InlineKeyboardRow(button);
    var markup = InlineKeyboardMarkup.builder()
      .keyboardRow(row)
      .build();
    String message = modelDto.getManufacturer() + ", " +
      modelDto.getName() + ", " +
      modelDto.getYear();
    return SendMessage.builder()
      .chatId(chatId)
      .text(message)
      .replyMarkup(markup)
      .build();
  }

  private SendMessage doUnderAllCars(long chatId) {
    InlineKeyboardButton button =
      InlineKeyboardButton.builder().text("funny moment").callbackData("callBack").build();
    InlineKeyboardMarkup inlineKeyboardMarkup =
      InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(button)).build();

    return SendMessage.builder()
      .text("it's working")
      .replyMarkup(inlineKeyboardMarkup)
      .chatId(chatId)
      .build();
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
      ReplyKeyboardMarkup.builder().keyboardRow(row1).keyboardRow(row2).build();
    message.setReplyMarkup(keyboardMarkup);
  }

  private void sendResponse(SendMessage message) {
    if (Objects.nonNull(message)) {
      try {
        telegramClient.execute(message);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void sendResponse(EditMessageText message) {
    if (Objects.nonNull(message)) {
      try {
        telegramClient.execute(message);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
