package ua.nicegear.cars.bot.view;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.dto.FilterDto;

public abstract class ViewMaker {

  public abstract SendMessage makeViewForUser(SendMessage sendMessage, FilterDto filterDto);
}
