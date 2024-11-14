package ua.nicegear.cars.bot.view;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.MainKeyboardButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;

@RequiredArgsConstructor
public class MainKeyboardViewMaker extends ViewMaker {

  private final ButtonNamesConfig buttonNames;

  @Override
  public SendMessage makeViewForUser(SendMessage sendMessage) {
    List<String> names =
        new ArrayList<>() {
          {
            add(buttonNames.getShowFilters());
            add(buttonNames.getStop());
          }
        };
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(new MainKeyboardButton(names));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
