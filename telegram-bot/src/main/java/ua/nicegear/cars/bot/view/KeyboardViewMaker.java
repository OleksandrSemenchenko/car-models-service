package ua.nicegear.cars.bot.view;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.BaseKeyboardButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;

@RequiredArgsConstructor
public class KeyboardViewMaker extends ViewMaker {

  private final ButtonNamesConfig buttonNames;

  @Override
  public SendMessage makeViewForUser(SendMessage sendMessage) {
    List<String> names = List.of(buttonNames.getShowFilters());
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(new BaseKeyboardButton(names));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
