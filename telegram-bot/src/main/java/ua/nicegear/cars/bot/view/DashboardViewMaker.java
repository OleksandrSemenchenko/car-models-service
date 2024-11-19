package ua.nicegear.cars.bot.view;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class DashboardViewMaker {

  public abstract SendMessage makeView(SendMessage message);
}
