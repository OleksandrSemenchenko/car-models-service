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
    createTextMessage(userId, sendMessage);
    ButtonMaker buttonMaker = new ButtonMaker();
    createMaxYearButton(buttonMaker, sendMessage);
    return sendMessage;
  }

  private void createMaxYearButton(ButtonMaker buttonMaker, SendMessage sendMessage) {
    LinkedHashMap<Object, Object> buttonDetails = new LinkedHashMap<>() {{
      put(buttonName.getMaxYear(), CallbackMessage.MAX_YEAR);
      put(buttonName.getDelete(), CallbackMessage.MAX_YEAR_CLEAN);
    }};
    buttonMaker.setButton(new InlineButton(buttonDetails));
    buttonMaker.makeButtonFor(sendMessage);
  }

  private void createTextMessage(long userId, SendMessage sendMessage) {
    FilterDto filterDto = filterService.getFilterByUserId(userId);
    String textMessage = "%s: %s, \n%s: %s".formatted(
      buttonName.getMaxYear(), filterDto.getMaxYear(),
      buttonName.getMaxMileage(), filterDto.getMaxMileage());
      sendMessage.setText(textMessage);
  }
}
