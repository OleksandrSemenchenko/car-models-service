package ua.nicegear.cars.bot.view.impl;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class BodyStyleDashboardViewMaker extends DashboardViewMaker {

  private final ButtonsConfig buttonsConfig;
  private InlineKeyboardMarkup markup = new InlineKeyboardMarkup(new ArrayList<>());

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    ButtonMaker buttonMaker = new ButtonMaker();
    buttonMaker.setButton(
        new InlineButton(
            buttonsConfig.getNames().getHatchback(), CallbackMessage.HATCHBACK, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(
        new InlineButton(buttonsConfig.getNames().getSedan(), CallbackMessage.SEDAN, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(
        new InlineButton(
            buttonsConfig.getNames().getCrossover(), CallbackMessage.CROSSOVER, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(
        new InlineButton(buttonsConfig.getNames().getMinivan(), CallbackMessage.MINIVAN, markup));
    buttonMaker.addButtonTo(sendMessage);
    buttonMaker.setButton(
        new InlineButton(
            buttonsConfig.getNames().getApply(), CallbackMessage.APPLY_BODY_STYLE, markup));
    buttonMaker.addButtonTo(sendMessage);
    return sendMessage;
  }
}
