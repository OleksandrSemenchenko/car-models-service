package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.BodyStyleDashboardView;

public class BodyStyleStrategy extends AbstractStrategy {

  private final ButtonsConfig buttonsConfig;

  public BodyStyleStrategy(TelegramClient telegramClient, ButtonsConfig buttonsConfig) {
    super(telegramClient);
    this.buttonsConfig = buttonsConfig;
  }

  @Override
  public void execute(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(chatId)
            .text(buttonsConfig.getPrompts().getBodyStyle())
            .build();
    super.processAnswerCallbackQuery(update, buttonsConfig.getPrompts().getBodyStyle());
    DashboardViewMaker bodyStyleViewMaker = new BodyStyleDashboardView(buttonsConfig);
    sendMessage = bodyStyleViewMaker.makeView(sendMessage);
    checkNext(sendMessage, update);
  }
}
