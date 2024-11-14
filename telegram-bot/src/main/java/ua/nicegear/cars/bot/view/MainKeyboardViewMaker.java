package ua.nicegear.cars.bot.view;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.MainKeyboardButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.dto.FilterDto;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MainKeyboardViewMaker extends ViewMaker {

  private final ButtonNamesConfig buttonNames;

  @Override
  public SendMessage makeViewForUser(SendMessage sendMessage) {
    ButtonMaker buttonMaker = new ButtonMaker();
    makeMainKeyboardView(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void makeMainKeyboardView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    List<String> names = new ArrayList<>() {{
      add(buttonNames.getShowFilters());
      add(buttonNames.getStop());
    }};
    buttonMaker.setButton(new MainKeyboardButton(names));
    buttonMaker.addButtonTo(sendMessage);
  }
}
