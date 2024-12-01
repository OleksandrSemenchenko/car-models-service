package ua.nicegear.cars.bot.controller.strategy.impl;

import static ua.nicegear.cars.bot.constants.CallbackMessage.APPLY_AND_SEARCH;
import static ua.nicegear.cars.bot.constants.CallbackMessage.BODY_STYLE;
import static ua.nicegear.cars.bot.constants.CallbackMessage.BODY_STYLE_APPLY;
import static ua.nicegear.cars.bot.constants.CallbackMessage.CROSSOVER;
import static ua.nicegear.cars.bot.constants.CallbackMessage.HATCHBACK;
import static ua.nicegear.cars.bot.constants.CallbackMessage.MAX_MILEAGE;
import static ua.nicegear.cars.bot.constants.CallbackMessage.MAX_YEAR;
import static ua.nicegear.cars.bot.constants.CallbackMessage.MINIVAN;
import static ua.nicegear.cars.bot.constants.CallbackMessage.MIN_YEAR;
import static ua.nicegear.cars.bot.constants.CallbackMessage.NUMBER_OF_OWNERS;
import static ua.nicegear.cars.bot.constants.CallbackMessage.SEDAN;

import java.util.Set;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.commands.button.CommandExecutor;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

public class CallbackQueryStrategy extends UpdateProcessor implements ConsumeStrategy {

  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  public CallbackQueryStrategy(
      TelegramClient telegramClient, ButtonsConfig buttonsConfig, FilterService filterService) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
    this.filterService = filterService;
  }

  @Override
  public void execute(Update update) {
    String command = update.getCallbackQuery().getData();
    Context context = new Context();
    CommandExecutor commandExecutor = new CommandExecutor(telegramClient, buttonsConfig, filterService, context);
    commandExecutor.execute(command);

    context = selectContextForBodyStyleFilter(update, context, command);

    context.executeStrategy(update);
  }

  private Context selectContextForBodyStyleFilter(
      Update update, Context context, String callbackMessage) {

    switch (callbackMessage) {
      case HATCHBACK -> {
        FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.HATCHBACK)).build();
        return getContextWithBodyStyleStrategy(update, context, filterDto);
      }
      case CROSSOVER -> {
        FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.CROSSOVER)).build();
        return getContextWithBodyStyleStrategy(update, context, filterDto);
      }
      case MINIVAN -> {
        FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.MINIVAN)).build();
        return getContextWithBodyStyleStrategy(update, context, filterDto);
      }
      case SEDAN -> {
        FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.SEDAN)).build();
        return getContextWithBodyStyleStrategy(update, context, filterDto);
      }
      case BODY_STYLE_APPLY -> {
        super.processAnswerCallbackQuery(update, CallbackMessage.BODY_STYLE_APPLY);
        SendMessage sendMessage = buildSendMessage(update);
        ConsumeStrategy searchDashboardStrategy =
            new SearchDashboardStrategy(telegramClient, filterService, buttonsConfig);
        context.setConsumeStrategy(searchDashboardStrategy);
      }
    }
    return context;
  }

  private Context getContextWithBodyStyleStrategy(
      Update update, Context context, FilterDto filterDto) {
    filterDto = filterService.updateCache(filterDto);
    SendMessage sendMessage = buildSendMessage(update, filterDto);
    ConsumeStrategy bodyStyleStrategy =
        new BodyStyleStrategy(telegramClient, buttonsConfig, filterService, sendMessage);
    context.setConsumeStrategy(bodyStyleStrategy);
    return context;
  }

  private SendMessage buildSendMessage(Update update, FilterDto filterDto) {
    long chartId = update.getCallbackQuery().getMessage().getChatId();
    String message =
        filterDto.getBodyStyles().stream().map(Enum::toString).collect(Collectors.joining(", "));
    return SendMessage.builder().chatId(chartId).text(message).build();
  }

  private SendMessage buildSendMessage(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    return new SendMessage(String.valueOf(chatId), "Lets get started");
  }
}
