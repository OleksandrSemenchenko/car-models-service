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
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.FilterDashboardViewMaker;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final FilterService filterService;
  private final ButtonNamesConfig buttonNames;
  private final CommandsConfig commandsConfig;

  @Override
  public void consume(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Hello world!");
    sendMessage = makeKeyboardView(sendMessage);
    makeMenuButtonView(chatId);

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
        DashboardViewMaker<SendMessage> filterDashboardViewMaker =
            new FilterDashboardViewMaker(buttonNames, filterDto, chatId);
        filterDashboardViewMaker.makeView(sendMessage);
      }
    }
    sendResponse(telegramClient::execute, sendMessage);
  }

  private SendMessage makeKeyboardView(SendMessage sendMessage) {
    DashboardViewMaker<SendMessage> dashboardViewMaker = new BaseDashboardViewMaker(buttonNames);
    return dashboardViewMaker.makeView(sendMessage);
  }

  private void makeMenuButtonView(long chatId) {
    MenuButtonViewMaker menuButtonViewMaker = new CommandsMenuButtonViewMaker(commandsConfig);
    MenuButtonView menuButtonView = menuButtonViewMaker.makeView(chatId);
    sendResponse(telegramClient::execute, menuButtonView.getMyCommands());
    sendResponse(telegramClient::execute, menuButtonView.getChatMenuButton());
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
