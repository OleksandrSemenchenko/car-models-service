package ua.nicegear.cars.bot.buttons;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class Button {

  protected abstract SendMessage addButtonTo(SendMessage sendMessage);
}
