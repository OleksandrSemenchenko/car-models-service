package ua.nicegear.cars.bot.controller.strategy.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.view.DashboardViewMaker;
import ua.nicegear.cars.bot.view.impl.BodyStyleDashboardView;

public class BodyStyleStrategy extends AbstractStrategy {

  private SendMessage sendMessage;

  public BodyStyleStrategy(TelegramClient telegramClient, SendMessage sendMessage) {
    super(telegramClient);
    this.sendMessage = sendMessage;
  }

  @Override
  public void execute(Update update) {
    DashboardViewMaker bodyStyleViewMaker = new BodyStyleDashboardView();
    sendMessage = bodyStyleViewMaker.makeView(sendMessage);
    checkNext(sendMessage, update);
  }
}
