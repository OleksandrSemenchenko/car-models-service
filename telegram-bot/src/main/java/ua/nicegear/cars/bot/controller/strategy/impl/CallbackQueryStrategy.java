package ua.nicegear.cars.bot.controller.strategy.impl;

import java.util.Set;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
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
    String callbackMessage = update.getCallbackQuery().getData();
    Context context = new Context();
    context = defineContextForSearchFilterCallbackdata(callbackMessage, context);

    // TODO externalize to a method
    if (callbackMessage.equals(BodyStyle.HATCHBACK.toString())) {
      FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.HATCHBACK)).build();
      context = defineContextForBodyStyle(update, context, filterDto);
    } else if (callbackMessage.equals(BodyStyle.CROSSOVER.toString())) {
      FilterDto filterDto = FilterDto.builder().bodyStyles(Set.of(BodyStyle.CROSSOVER)).build();
      context = defineContextForBodyStyle(update, context, filterDto);
    }
    context.executeStrategy(update);
  }

  private Context defineContextForSearchFilterCallbackdata(
      String callbackMessage, Context context) {
    String message = "";

    if (callbackMessage.equals(buttonsConfig.getNames().getMaxYear())) {
      message = buttonsConfig.getPrompts().getMaxYear();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMinYear())) {
      message = buttonsConfig.getPrompts().getMinYear();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getMaxMileage())) {
      message = buttonsConfig.getPrompts().getMaxMileage();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getNumberOfOwners())) {
      message = buttonsConfig.getPrompts().getNumberOfOwners();
      context.setConsumeStrategy(new ForceReplyStrategy(telegramClient, message));
    } else if (callbackMessage.equals(buttonsConfig.getNames().getBodyStyle())) {
      ConsumeStrategy bodyStyleStrategy = new BodyStyleStrategy(telegramClient, buttonsConfig);
      context.setConsumeStrategy(bodyStyleStrategy);
    }
    if (callbackMessage.equals(buttonsConfig.getNames().getApplyAndSearch())) {
      // TODO ApplyAndSearch actions
    }
    return context;
  }

  private SendMessage buildSendMessage(Update update, FilterDto filterDto) {
    long chartId = update.getCallbackQuery().getMessage().getChatId();
    String message =
        filterDto.getBodyStyles().stream().map(Enum::toString).collect(Collectors.joining(", "));
    return SendMessage.builder().chatId(chartId).text(message).build();
  }

  private Context defineContextForBodyStyle(Update update, Context context, FilterDto filterDto) {
    filterDto = filterService.saveToCache(filterDto);
    SendMessage sendMessage = buildSendMessage(update, filterDto);
    ConsumeStrategy bodyStyleStrategy =
        new BodyStyleStrategy(telegramClient, buttonsConfig, sendMessage);
    context.setConsumeStrategy(bodyStyleStrategy);
    return context;
  }
}
