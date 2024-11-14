package ua.nicegear.cars.bot.view;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.nicegear.cars.bot.buttons.ButtonMaker;
import ua.nicegear.cars.bot.buttons.impl.InlineButton;
import ua.nicegear.cars.bot.config.ButtonNamesConfig;
import ua.nicegear.cars.bot.constants.CallbackMessage;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.service.FilterService;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
public class FilterViewMaker extends ViewMaker {

  private final FilterService filterService;
  private final ButtonNamesConfig buttonName;

  @Override
  public SendMessage makeViewForUser(SendMessage sendMessage, long userId) {
    makeTextMessageView(userId, sendMessage);
    ButtonMaker buttonMaker = new ButtonMaker();
    makeMaxYearFilterView(buttonMaker, sendMessage);
    makeMinYearFilterView(buttonMaker, sendMessage);
    makeMaxMillageFilterView(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void makeTextMessageView(long userId, SendMessage sendMessage) {
    FilterDto filterDto = filterService.getFilterByUserId(userId);
    String textMessage = """
      %s: %s,
      %s: %s,
      %s: %s,
      %s: %s
      """.formatted(
      buttonName.getMaxYear(), filterDto.getMaxYear(),
      buttonName.getMinYear(), filterDto.getMinYear(),
      buttonName.getMaxMileage(), filterDto.getMaxMileage(),
      buttonName.getNumberOfOwners(), filterDto.getNumberOfOwners());
    sendMessage.setText(textMessage);
  }

  private void makeMaxYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>() {{
      put(buttonName.getMaxYear(), CallbackMessage.MAX_YEAR);
      put(buttonName.getDelete(), CallbackMessage.MAX_YEAR_DELETE);
    }};
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMinYearFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>() {{
      put(buttonName.getMinYear(), CallbackMessage.MIN_YEAR);
      put(buttonName.getDelete(), CallbackMessage.MIN_YEAR_DELETE);
    }};
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeMaxMillageFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>() {{
      put(buttonName.getMaxMileage(), CallbackMessage.MIN_MILLAGE);
      put(buttonName.getDelete(), CallbackMessage.MIN_MILLAGE_DELETE);
    }};
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }

  private void makeNumberOfOwnersFilterView(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>() {{
      put(buttonName.getNumberOfOwners(), CallbackMessage.NUMBER_OF_OWNERS);
      put(buttonName.getDelete(), CallbackMessage.NUMBER_OF_OWNERS_DELETE);
    }};
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.addButtonTo(sendMessage);
  }
}
