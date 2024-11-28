package ua.nicegear.cars.bot.controller.strategy;

import io.micrometer.observation.Observation;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
public abstract class UpdateProcessor {

  protected final TelegramClient telegramClient;

  //  protected final FilterService filterService;
  //  protected final ButtonsConfig buttonsConfig;

  /* protected void processAnswerCallbackQuery(Update update, String message) {
    AnswerCallbackQuery answerCallbackQuery =
        AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text(message)
            .build();
    processResponse(telegramClient::execute, answerCallbackQuery);
  }*/

  /*protected void processForceReplyKeyboard(Update update, String message) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .replyMarkup(forceReplyKeyboard)
            .text(message)
            .build();
    processResponse(telegramClient::execute, sendMessage);
  }*/

  protected <T, R, E extends TelegramApiException> R processResponse(
      Observation.CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*protected SendMessage makeSearchDashboardView(SendMessage sendMessage, long chatId) {
    FilterDto searchFiltersDto = filterService.getSearchFilterByChatId(chatId);
    DashboardViewMaker searchDashboardViewMaker =
        new SearchDashboardViewMaker(buttonsConfig, searchFiltersDto);
    return searchDashboardViewMaker.makeView(sendMessage);
  }*/
}
