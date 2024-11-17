package ua.nicegear.cars.bot.view.impl;


import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.BaseKeyboardButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.view.ButtonViewMaker;

import java.util.List;

@RequiredArgsConstructor
public class KeyboardButtonViewMaker extends ButtonViewMaker<SendMessage> {

  private final ButtonNamesConfig buttonNames;

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    List<String> names = List.of(buttonNames.getShowFilters());
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(new BaseKeyboardButton(names));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
