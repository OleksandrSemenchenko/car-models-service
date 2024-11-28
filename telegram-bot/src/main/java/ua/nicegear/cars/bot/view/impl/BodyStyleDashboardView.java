package ua.nicegear.cars.bot.view.impl;

import static ua.nicegear.cars.bot.enums.BodyStyle.CROSSOVER;
import static ua.nicegear.cars.bot.enums.BodyStyle.HATCHBACK;
import static ua.nicegear.cars.bot.enums.BodyStyle.MINIVAN;
import static ua.nicegear.cars.bot.enums.BodyStyle.SEDAN;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class BodyStyleDashboardView extends DashboardViewMaker {

  private final ButtonsConfig buttonsConfig;
  private InlineKeyboardMarkup markup = new InlineKeyboardMarkup(new ArrayList<>());

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(new InlineButton(HATCHBACK, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(new InlineButton(SEDAN, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(new InlineButton(CROSSOVER, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(new InlineButton(MINIVAN, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(new InlineButton(buttonsConfig.getNames().getApply(), markup));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
