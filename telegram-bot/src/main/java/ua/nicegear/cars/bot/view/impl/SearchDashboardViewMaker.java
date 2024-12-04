package ua.nicegear.cars.bot.view.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class SearchDashboardViewMaker extends DashboardViewMaker {

  private final ButtonsConfig buttonsConfig;
  private final FilterDto filterDto;

  private InlineKeyboardMarkup markup = new InlineKeyboardMarkup(new ArrayList<>());

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    makeTextMessageView(filterDto, sendMessage);
    ButtonMaker buttonMaker = new ButtonMaker();
    makeMaxYearFilterView(buttonMaker, sendMessage);
    makeMinYearFilterView(buttonMaker, sendMessage);
    makeMaxMillageFilterView(buttonMaker, sendMessage);
    makeNumberOfOwnersFilterView(buttonMaker, sendMessage);
    makeBodyStyleFilterView(buttonMaker, sendMessage);
    makeApplyAndSearchButtonView(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void makeTextMessageView(FilterDto filterDto, SendMessage sendMessage) {
    String textMessage =
        """
      %s: %s
      %s: %s
      %s: %s
      %s: %s
      %s: %s
      """
            .formatted(
                buttonsConfig.getNames().getMaxYear(), checkForNull(filterDto.getMaxYear()),
                buttonsConfig.getNames().getMinYear(), checkForNull(filterDto.getMinYear()),
                buttonsConfig.getNames().getMaxMileage(), checkForNull(filterDto.getMaxMileage()),
                buttonsConfig.getNames().getNumberOfOwners(),
                    checkForNull(filterDto.getNumberOfOwners()),
                buttonsConfig.getNames().getBodyStyle(), checkForNull(filterDto.getBodyStyles()));
    sendMessage.setText(textMessage);
  }

  private String checkForNull(Object object) {
    if (Objects.isNull(object)) {
      return buttonsConfig.getPrompts().getNullValue();
    }
    return String.valueOf(object);
  }

  private void makeMaxYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getMaxYear(), CallbackMessage.MAX_YEAR);
            put(buttonsConfig.getNames().getDelete(), CallbackMessage.MAX_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMinYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getMinYear(), CallbackMessage.MIN_YEAR);
            put(buttonsConfig.getNames().getDelete(), CallbackMessage.MIN_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMaxMillageFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getMaxMileage(), CallbackMessage.MAX_MILEAGE);
            put(buttonsConfig.getNames().getDelete(), CallbackMessage.MAX_MILLAGE_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeNumberOfOwnersFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getNumberOfOwners(), CallbackMessage.NUMBER_OF_OWNERS);
            put(buttonsConfig.getNames().getDelete(), CallbackMessage.NUMBER_OF_OWNERS_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeBodyStyleFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getBodyStyle(), CallbackMessage.BODY_STYLE);
            put(buttonsConfig.getNames().getDelete(), CallbackMessage.BODY_STYLE_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeApplyAndSearchButtonView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonsConfig.getNames().getApplyAndSearch(), CallbackMessage.APPLY_AND_SEARCH);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }
}
