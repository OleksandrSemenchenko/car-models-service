package ua.nicegear.cars.bot.buttons.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

public class InlineButton extends Button {

  private final InlineKeyboardMarkup markup;
  private LinkedHashMap<Object, Object> buttonDetails;
  private Object buttonName;

  public InlineButton(LinkedHashMap<Object, Object> buttonDetails, InlineKeyboardMarkup markup) {
    this.buttonDetails = buttonDetails;
    this.markup = markup;
  }

  public InlineButton(Object buttonName, InlineKeyboardMarkup markup) {
    this.buttonName = buttonName;
    this.markup = markup;
  }

  @Override
  public SendMessage addButtonTo(SendMessage sendMessage) {
    var buttons = buildButtons();
    var row = new InlineKeyboardRow(buttons);
    markup.getKeyboard().add(row);
    sendMessage.setReplyMarkup(markup);
    return sendMessage;
  }

  private List<InlineKeyboardButton> buildButtons() {
    if (Objects.nonNull(buttonDetails)) {
      return buttonDetails.entrySet().stream()
          .map(
              entry ->
                  InlineKeyboardButton.builder()
                      .text(String.valueOf(entry.getKey()))
                      .callbackData(String.valueOf(entry.getValue()))
                      .build())
          .collect(Collectors.toList());
    } else {
      InlineKeyboardButton button =
          InlineKeyboardButton.builder()
              .text(String.valueOf(buttonName))
              .callbackData(String.valueOf(buttonName))
              .build();
      return List.of(button, button);
    }
  }
}
