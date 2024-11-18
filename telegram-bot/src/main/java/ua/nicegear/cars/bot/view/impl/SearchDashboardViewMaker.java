package ua.nicegear.cars.bot.view.impl;

import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.view.DashboardViewMaker;

@RequiredArgsConstructor
public class SearchDashboardViewMaker extends DashboardViewMaker<SendMessage> {

  private final ButtonNamesConfig buttonNames;
  private final SearchFilterDto searchFilterDto;

  @Override
  public SendMessage makeView(SendMessage sendMessage) {
    makeTextMessageView(searchFilterDto, sendMessage);
    ButtonMaker buttonMaker = new ButtonMaker();
    makeMaxYearFilterView(buttonMaker, sendMessage);
    makeMinYearFilterView(buttonMaker, sendMessage);
    makeMaxMillageFilterView(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void makeTextMessageView(SearchFilterDto searchFilterDto, SendMessage sendMessage) {
    String textMessage =
        """
      %s: %s,
      %s: %s,
      %s: %s,
      %s: %s
      """
            .formatted(
                buttonNames.getMaxYear(), searchFilterDto.getMaxYear(),
                buttonNames.getMinYear(), searchFilterDto.getMinYear(),
                buttonNames.getMaxMileage(), searchFilterDto.getMaxMileage(),
                buttonNames.getNumberOfOwners(), searchFilterDto.getNumberOfOwners());
    sendMessage.setText(textMessage);
  }

  private void makeMaxYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMaxYear(), CallbackMessage.MAX_YEAR);
            put(buttonNames.getDelete(), CallbackMessage.MAX_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMinYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMinYear(), CallbackMessage.MIN_YEAR);
            put(buttonNames.getDelete(), CallbackMessage.MIN_YEAR_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMaxMillageFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getMaxMileage(), CallbackMessage.MIN_MILLAGE);
            put(buttonNames.getDelete(), CallbackMessage.MIN_MILLAGE_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeNumberOfOwnersFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails =
        new LinkedHashMap<>() {
          {
            put(buttonNames.getNumberOfOwners(), CallbackMessage.NUMBER_OF_OWNERS);
            put(buttonNames.getDelete(), CallbackMessage.NUMBER_OF_OWNERS_DELETE);
          }
        };
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }
}
