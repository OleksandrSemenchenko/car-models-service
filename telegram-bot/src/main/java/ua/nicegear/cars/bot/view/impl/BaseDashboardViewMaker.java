package ua.nicegear.cars.bot.view.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.BaseKeyboardButton;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class BaseDashboardViewMaker extends DashboardViewMaker {

  private final ButtonsConfig buttonsConfig;

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    List<String> buttonNames = List.of(this.buttonsConfig.getNames().getSearchDashboard());
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(new BaseKeyboardButton(buttonNames));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
