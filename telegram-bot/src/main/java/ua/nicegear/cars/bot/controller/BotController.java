package ua.nicegear.cars.bot.controller;

import io.micrometer.observation.Observation.CheckedFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.buttons.ResponseContext;
import ua.nicegear.cars.bot.buttons.impl.DetailsInlineKeyboardButton;
import ua.nicegear.cars.bot.constants.CallbackMessage;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final ResponseContext responseContext = new ResponseContext();

  @Override
  public void consume(Update update) {
    long chatId = update.getMessage().getChatId();
//    String receivedMessage = update.getMessage().getText();
    SendMessage sendMessage = buildSendMessage(update);

    /*SetChatMenuButton setChatMenuButton = new SetChatMenuButton();
    BotCommand command = new BotCommand("/start", "start");
    BotCommand command1 = new BotCommand("/delete", "delete");
    SetMyCommands setMyCommands = new SetMyCommands(List.of(command, command1));
    sendResponse(telegramClient::execute, setMyCommands);

    setChatMenuButton.setMenuButton(MenuButtonCommands.builder().build());
    setChatMenuButton.setChatId(chatId);
    sendResponse(telegramClient::execute, setChatMenuButton);*/

    ForceReplyKeyboard forceReply = new ForceReplyKeyboard(true);
    sendMessage.setText("reply");
    sendMessage.setReplyMarkup(forceReply);
    sendResponse(telegramClient::execute, sendMessage);

    if (update.hasCallbackQuery()) {
      String callback = update.getCallbackQuery().getData();

      if (callback.equals(CallbackMessage.DETAILS_BUTTON)) {
        responseContext.setButton(new DetailsInlineKeyboardButton());
        sendMessage = responseContext.addInlineButtonTo(sendMessage);
      }
    }
  }

  private SendMessage buildSendMessage(Update update) {
    long chatId = update.getMessage().getChatId();
    return SendMessage.builder()
      .text("Hello World!")
//      .replyMarkup(new ReplyKeyboardRemove(true))
      .chatId(chatId)
      .build();
  }

  private <T, R, E extends TelegramApiException> R sendResponse(CheckedFunction<T, R, E> consumer,
                                                                T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
