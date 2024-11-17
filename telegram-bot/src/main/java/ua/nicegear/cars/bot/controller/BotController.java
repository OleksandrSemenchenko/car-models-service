package ua.nicegear.cars.bot.controller;

import io.micrometer.observation.Observation.CheckedFunction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.config.CommandNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.ButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.FilterButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.KeyboardButtonViewMaker;

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
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Hello world!");
    sendMessage = addKeyboardViewTo(sendMessage);
    addMenuViewForChat(chatId);

    String callbackMessage = update.getMessage().getText();

    if (callbackMessage.equals(CallbackMessage.SHOW_FILTERS)) {
      // TODO
      sendMessage.setText("TODO");
    }

    if (update.hasCallbackQuery()) {
      String callbackData = update.getCallbackQuery().getData();
      long userId = update.getCallbackQuery().getFrom().getId();

      if (callbackData.equals(CallbackMessage.SHOW_FILTERS)) {
        sendMessage.setText("I see you message");
        FilterDto filterDto = filterService.getFilterByUserId(userId);
        ButtonViewMaker<SendMessage> filterButtonViewMaker =
            new FilterButtonViewMaker(buttonNames, filterDto, chatId);
        filterButtonViewMaker.makeView(sendMessage);
      }
    }
    sendResponse(telegramClient::execute, sendMessage);
  }

  private void addMenuViewForChat(long chatId) {
    BotCommand botCommand =
        BotCommand.builder().command("fire").description("fire command").build();
    SetMyCommands myCommands = SetMyCommands.builder().command(botCommand).build();
    sendResponse(telegramClient::execute, myCommands);

    SetChatMenuButton commandsMenuButton =
        SetChatMenuButton.builder().menuButton(new MenuButtonCommands()).chatId(chatId).build();
    sendResponse(telegramClient::execute, commandsMenuButton);
  }

  private void addStopCommandForChat() {
    BotCommand botCommand =
        BotCommand.builder().command("stop").description("stop command").build();
    SetMyCommands myCommands = new SetMyCommands(List.of(botCommand));
    sendResponse(telegramClient::execute, myCommands);
  }

  private SendMessage addKeyboardViewTo(SendMessage sendMessage) {
    ButtonViewMaker<SendMessage> keyboardButtonViewMaker = new KeyboardButtonViewMaker(buttonNames);
    return keyboardButtonViewMaker.makeView(sendMessage);
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
