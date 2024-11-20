package ua.nicegear.cars.bot.controller;

import io.micrometer.observation.Observation.CheckedFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.service.SearchFilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.BaseDashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.CommandsMenuButtonViewMaker;
import ua.nicegear.cars.bot.view.impl.SearchDashboardViewMaker;

@Component
@RequiredArgsConstructor
public class BotController implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final SearchFilterService searchFilterService;
  private final ButtonNamesConfig buttonNames;
  private final CommandsConfig commandsConfig;

  @Override
  public void consume(Update update) {
    long chatId;
    SendMessage sendMessage;
    String callbackMessage = "";

    if (update.hasCallbackQuery()) {
      String callbackData = update.getCallbackQuery().getData();
      chatId = update.getCallbackQuery().getMessage().getChatId();
      sendMessage = new SendMessage(String.valueOf(chatId), "");

      if (callbackData.equals(buttonNames.getMaxYear())) {
        AnswerCallbackQuery answerCallbackQuery =
            AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Set some text")
                .build();
        processResponse(telegramClient::execute, answerCallbackQuery);
        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);
        sendMessage.setReplyMarkup(forceReplyKeyboard);
        sendMessage.setText("Please provide the max year:");
      } else if (callbackData.equals(buttonNames.getMinYear())) {
        // TODO
      }
    } else if (update.getMessage().isReply()) {
      String repliedMessage = update.getMessage().getReplyToMessage().getText();
      chatId = update.getMessage().getChatId();
      sendMessage = new SendMessage(String.valueOf(chatId), "");

      if (repliedMessage.equals("Please provide the max year:")) {
        // TODO
        sendMessage.setText("is reply");
      }
    } else {
      chatId = update.getMessage().getChatId();
      sendMessage = new SendMessage(String.valueOf(chatId), "");
      callbackMessage = update.getMessage().getText();
      sendMessage = makeBaseDashboardView(sendMessage);
      addMenuButtonViewAndProcessResponse(chatId);

      if (callbackMessage.equals(CallbackMessage.STOP_COMMAND)) {
        // TODO
        sendMessage.setText("TODO");
      } else if (callbackMessage.equals(buttonNames.getSearchDashboard())) {
        sendMessage = makeSearchDashboardView(sendMessage, chatId);
      }
    }
    processResponse(telegramClient::execute, sendMessage);
  }

  private SendMessage makeBaseDashboardView(SendMessage sendMessage) {
    DashboardViewMaker dashboardViewMaker = new BaseDashboardViewMaker(buttonNames);
    return dashboardViewMaker.makeView(sendMessage);
  }

  private void addMenuButtonViewAndProcessResponse(long chatId) {
    MenuButtonViewMaker menuButtonViewMaker = new CommandsMenuButtonViewMaker(commandsConfig);
    MenuButtonView menuButtonView = menuButtonViewMaker.makeView(chatId);
    processResponse(telegramClient::execute, menuButtonView.getMyCommands());
    processResponse(telegramClient::execute, menuButtonView.getChatMenuButton());
  }

  private SendMessage makeSearchDashboardView(SendMessage sendMessage, long chatId) {
    SearchFilterDto searchFiltersDto = searchFilterService.getSearchFilterByChatId(chatId);
    DashboardViewMaker searchDashboardViewMaker =
        new SearchDashboardViewMaker(buttonNames, searchFiltersDto);
    return searchDashboardViewMaker.makeView(sendMessage);
  }

  private <T, R, E extends TelegramApiException> R processResponse(
      CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
