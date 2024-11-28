package ua.nicegear.cars.bot.controller.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;

@Slf4j
public class ReplyStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  public ReplyStrategy(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
    this.filterService = filterService;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("").build();
    AbstractStrategy sendMessageStrategy = new SendMessageStrategy(telegramClient, sendMessage);
    String repliedMessage = update.getMessage().getReplyToMessage().getText();
    String message = update.getMessage().getText();

    if (update.getMessage().isReply()) {
      FilterDto filterDto = buildFilterDto(repliedMessage, message);
      filterService.saveToCache(filterDto);
      AbstractStrategy searchDashboardStrategy =
          new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig, sendMessage);
      sendMessageStrategy.add(searchDashboardStrategy);
    } else if (repliedMessage.equals(buttonsConfig.getPrompts().getBodyStyle())) {
      AbstractStrategy bodyStyleStrategy = new BodyStyleStrategy(telegramClient, sendMessage);
      sendMessageStrategy.add(bodyStyleStrategy);
    }
    sendMessageStrategy.execute(update);
  }

  private FilterDto buildFilterDto(String repliedMessage, String message) {
    FilterDto filterDto = new FilterDto();

    if (repliedMessage.equals(buttonsConfig.getPrompts().getMaxYear())) {
      int maxYear = handleDigitInput(message);
      filterDto.setMaxYear(maxYear);
    } else if (repliedMessage.equals(buttonsConfig.getPrompts().getMinYear())) {
      int minYear = handleDigitInput(message);
      filterDto.setMinYear(minYear);
    } else if (repliedMessage.equals(buttonsConfig.getPrompts().getMaxMileage())) {
      int maxMileage = handleDigitInput(message);
      filterDto.setMaxMileage(maxMileage);
    } else if (repliedMessage.equals(buttonsConfig.getPrompts().getNumberOfOwners())) {
      int numberOfOwners = handleDigitInput(message);
      filterDto.setNumberOfOwners(numberOfOwners);
    }
    return filterDto;
  }

  private int handleDigitInput(String message) {
    int year;
    try {
      year = Integer.parseInt(message);
    } catch (NumberFormatException e) {
      String exceptionMessage =
          "The input '%s' for the digit is wrong and replaced by '0'".formatted(message);
      log.debug(exceptionMessage);
      year = 0;
    }
    return year;
  }
}
