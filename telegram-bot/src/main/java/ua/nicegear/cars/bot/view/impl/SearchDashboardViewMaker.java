package ua.nicegear.cars.bot.view.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class SearchDashboardViewMaker extends DashboardViewMaker {

  private final ButtonNamesConfig buttonNames;
  private final SearchFilterDto searchFilterDto;

  private InlineKeyboardMarkup markup = new InlineKeyboardMarkup(new ArrayList<>());

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    makeTextMessageView(searchFilterDto, sendMessage);
    ButtonMaker buttonMaker = new ButtonMaker();
    makeMaxYearFilterView(buttonMaker, sendMessage);
    makeMinYearFilterView(buttonMaker, sendMessage);
    makeMaxMillageFilterView(buttonMaker, sendMessage);
    makeNumberOfOwnersFilterView(buttonMaker, sendMessage);
    makeBodyStyleFilterView(buttonMaker, sendMessage);
    makeApplyAndSearchButtonView(buttonMaker, sendMessage);
    makeCloseButtonView(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void makeTextMessageView(SearchFilterDto searchFilterDto, SendMessage sendMessage) {
    String textMessage =
        """
      %s: %s
      %s: %s
      %s: %s
      %s: %s
      %s: %s
      """
            .formatted(
                buttonNames.getMaxYear(), searchFilterDto.getMaxYear(),
                buttonNames.getMinYear(), searchFilterDto.getMinYear(),
                buttonNames.getMaxMileage(), searchFilterDto.getMaxMileage(),
                buttonNames.getNumberOfOwners(), searchFilterDto.getNumberOfOwners(),
                buttonNames.getBodyStyle(), searchFilterDto.getBodyStyle());
    sendMessage.setText(textMessage);
  }

  private void makeMaxYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMaxYear(), buttonNames.getMaxYear());
            put(buttonNames.getDelete(), CallbackMessage.MAX_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMinYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMinYear(), buttonNames.getMinYear());
            put(buttonNames.getDelete(), CallbackMessage.MIN_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMaxMillageFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMaxMileage(), buttonNames.getMaxMileage());
            put(buttonNames.getDelete(), CallbackMessage.MAX_MILLAGE_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeNumberOfOwnersFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getNumberOfOwners(), buttonNames.getNumberOfOwners());
            put(buttonNames.getDelete(), CallbackMessage.NUMBER_OF_OWNERS_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeBodyStyleFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getBodyStyle(), buttonNames.getBodyStyle());
            put(buttonNames.getDelete(), CallbackMessage.BODY_STYLE_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeApplyAndSearchButtonView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>();
    buttonDetails.put(buttonNames.getApplyAndSearch(), buttonNames.getApplyAndSearch());
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeCloseButtonView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>();
    buttonDetails.put(buttonNames.getClose(), buttonNames.getClose());
    buttonMaker.setButton(new InlineButton(buttonDetails, markup));
    buttonMaker.addButtonTo(sendMessage);
  }
}
