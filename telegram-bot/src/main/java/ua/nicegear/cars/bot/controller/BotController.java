package ua.nicegear.cars.bot.controller;

import io.micrometer.observation.Observation.CheckedFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.FilterViewMaker;
import ua.nicegear.cars.bot.view.KeyboardViewMaker;
import ua.nicegear.cars.bot.view.ViewMaker;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final FilterService filterService;
  private final ButtonNamesConfig buttonNames;

  @Override
  public void consume(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = buildSendMessage(chatId);

    ViewMaker mainKeyboardViewMaker = new KeyboardViewMaker(buttonNames);
    mainKeyboardViewMaker.makeViewForUser(sendMessage);
    sendResponse(telegramClient::execute, sendMessage);

    if (update.hasCallbackQuery()) {
      String callbackData = update.getCallbackQuery().getData();
      long userId = update.getCallbackQuery().getFrom().getId();

      if (callbackData.equals(CallbackMessage.FILTER)) {
        FilterDto filterDto = filterService.getFilterByUserId(userId);
        ViewMaker filterViewMaker = new FilterViewMaker(buttonNames, filterDto);
        filterViewMaker.makeViewForUser(sendMessage);
        sendResponse(telegramClient::execute, sendMessage);
      }
    }
  }

  private SendMessage buildSendMessage(long chatId) {
    return SendMessage.builder().text("Hello World!").chatId(chatId).build();
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
