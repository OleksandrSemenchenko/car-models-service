package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;

public class ReplyConsumeStrategy extends UpdateProcessor implements ConsumeStrategy {

  public ReplyConsumeStrategy(
      TelegramClient telegramClient, FilterService filterService, ButtonsConfig buttonsConfig) {
    super(telegramClient, filterService, buttonsConfig);
  }

  @Override
  public void execute(Update update) {
    String repliedMessage = update.getMessage().getReplyToMessage().getText();
    long chatId = update.getMessage().getChatId();
    String message = update.getMessage().getText();

    if (repliedMessage.equals(super.buttonsConfig.getPrompts().getMaxYear())) {
      int maxYear;
      try {
        maxYear = Integer.parseInt(message);
      } catch (NumberFormatException e) {
        maxYear = 0;
      }
      FilterDto filterDto = FilterDto.builder().maxYear(maxYear).build();
      super.filterService.saveToCache(filterDto);
      SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("").build();
      sendMessage = makeSearchDashboardView(sendMessage, chatId);
      super.processUpdate(telegramClient::execute, sendMessage);
    }
  }
}
