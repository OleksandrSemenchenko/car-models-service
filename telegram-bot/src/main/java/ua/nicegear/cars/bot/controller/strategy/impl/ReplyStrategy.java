package ua.nicegear.cars.bot.controller.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.controller.strategy.ResponseProcessor;
import ua.nicegear.cars.bot.controller.strategy.Strategy;

@RequiredArgsConstructor
public class ReplyStrategy extends ResponseProcessor implements Strategy {

  private final TelegramClient telegramClient;

  @Override
  public void execute(Update update) {
    String repliedMessage = update.getMessage().getReplyToMessage().getText();
    long chatId = update.getMessage().getChatId();

    if (repliedMessage.equals("Please provide the max year:")) {
      // TODO save the filter value to bd or cash

      SendMessage sendMessage =
          SendMessage.builder()
              .chatId(String.valueOf(chatId))
              .text("save the filter value to bd or cash")
              .build();
      super.processResponse(telegramClient::execute, sendMessage);
    }
  }
}
