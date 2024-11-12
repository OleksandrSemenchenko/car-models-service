package ua.nicegear.cars.bot.view;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class ViewMaker {

  public abstract SendMessage makeViewForUser(SendMessage sendMessage, long userId);
}
