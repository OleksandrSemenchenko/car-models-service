package ua.nicegear.cars.bot.controller.strategy.impl;

import java.util.Objects;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.BodyStyleDashboardViewMaker;

public class BodyStyleStrategy extends AbstractStrategy {

  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  public BodyStyleStrategy(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
    this.filterService = filterService;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();

    FilterDto filterDto = filterService.getFiltersByChatId(chatId);
    String text = buttonsConfig.getPrompts().getBodyStyle();

    if (Objects.nonNull(filterDto.getBodyStyles())) {
      text =
          filterDto.getBodyStyles().stream()
              .map(BodyStyle::toString)
              .collect(Collectors.joining(", "));
    }
    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(text).build();
    super.processAnswerCallbackQuery(update, buttonsConfig.getPrompts().getBodyStyle());
    DashboardViewMaker bodyStyleViewMaker = new BodyStyleDashboardViewMaker(buttonsConfig);
    sendMessage = bodyStyleViewMaker.makeView(sendMessage);
    checkNext(sendMessage, update);
  }
}
