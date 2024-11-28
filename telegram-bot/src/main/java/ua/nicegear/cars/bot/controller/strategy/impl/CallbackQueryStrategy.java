package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.UpdateProcessor;
import ua.nicegear.cars.bot.service.FilterService;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.BodyStyleDashboardView;

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
    String message = "";
    Context context = new Context();

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
      // TODO should return view
      //      message = buttonsConfig.getPrompts().getBodyStyle();
      long chatId = update.getMessage().getChatId();
      SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("").build();
      DashboardViewMaker bodyStyleViewMaker = new BodyStyleDashboardView();
      sendMessage = bodyStyleViewMaker.makeView(sendMessage);
      this.processResponse(telegramClient::execute, sendMessage);

    } else if (callbackMessage.equals(buttonsConfig.getNames().getApplyAndSearch())) {
      // TODO ApplyAndSearch actions

    } else if (callbackMessage.equals(buttonsConfig.getNames().getClose())) {
      // TODO Close actions

    }
  }
}
