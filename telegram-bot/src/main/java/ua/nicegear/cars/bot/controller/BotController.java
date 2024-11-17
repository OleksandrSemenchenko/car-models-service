package ua.nicegear.cars.bot.controller;

import io.micrometer.observation.Observation.CheckedFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.config.CommandNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.impl.CommandMenuViewMaker;
import ua.nicegear.cars.bot.view.impl.FilterButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.KeyboardButtonViewMaker;
import ua.nicegear.cars.bot.view.ButtonViewMaker;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final FilterService filterService;
  private final ButtonNamesConfig buttonNames;
  private final CommandNamesConfig commandNames;

  @Override
  public void consume(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "hello world");
    sendMessage = addKeyboardViewTo(sendMessage);

    SetChatMenuButton commandChatMenuButton = new SetChatMenuButton();
    commandChatMenuButton = addMenuViewForChat(commandChatMenuButton, chatId);
    SetMyCommands setMyCommands = addCommandForChat();




    if (update.hasCallbackQuery()) {
      String callbackData = update.getCallbackQuery().getData();
      long userId = update.getCallbackQuery().getFrom().getId();

      if (callbackData.equals(CallbackMessage.FILTER)) {
        FilterDto filterDto = filterService.getFilterByUserId(userId);
        ButtonViewMaker<SendMessage> filterButtonViewMaker = new FilterButtonViewMaker(buttonNames, filterDto, chatId);
//        filterViewMaker.makeViewForUser(sendMessage);
//        sendResponse(telegramClient::execute, sendMessage);
      }
    }
    sendResponse(telegramClient::execute, setMyCommands);
    sendResponse(telegramClient::execute, commandChatMenuButton);
    sendResponse(telegramClient::execute, sendMessage);
  }

  private SetChatMenuButton addMenuViewForChat(SetChatMenuButton setChatMenuButton, long chatId) {
    ButtonViewMaker<SetChatMenuButton> commandMenuViewMaker = new CommandMenuViewMaker(chatId);
    return commandMenuViewMaker.makeView(setChatMenuButton);
  }

  private SetMyCommands addCommandForChat() {
    BotCommand stopCommand = BotCommand.builder()
      .command(commandNames.getStop())
      .description("new command")
      .build();
    return new SetMyCommands(List.of(stopCommand));
  }

  private SendMessage addKeyboardViewTo(SendMessage sendMessage) {
    ButtonViewMaker<SendMessage> keyboardButtonViewMaker = new KeyboardButtonViewMaker(buttonNames);
    return keyboardButtonViewMaker.makeView(sendMessage);
  }

  private SendMessage buildSendMessage(long chatId) {
    return SendMessage.builder().text("Hello World!")
      .chatId(chatId).build();
  }

  private <T, R, E extends TelegramApiException> R sendResponse(
      CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
