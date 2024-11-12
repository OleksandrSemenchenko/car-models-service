package ua.nicegear.cars.bot.buttons.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InlineButton extends Button {

  public LinkedHashMap<Object, Object> buttonAttributes;

  public InlineButton(String name, String callbackMessage) {
    this.buttonAttributes.put(name, callbackMessage);
  }

  public InlineButton(LinkedHashMap<Object, Object> buttonAttributes) {
    this.buttonAttributes = buttonAttributes;
  }

  @Override
  public SendMessage addButtonTo(SendMessage sendMessage) {
    var buttons = buildButtons();
    var row = new InlineKeyboardRow(buttons);
    markup = addRowToMarkup(row);
    sendMessage.setReplyMarkup(markup);
    return sendMessage;
  }

  private List<InlineKeyboardButton> buildButtons() {
    return buttonAttributes.entrySet().stream()
      .map(entry -> InlineKeyboardButton.builder()
        .text(String.valueOf(entry.getKey()))
        .callbackData(String.valueOf(entry.getValue()))
        .build())
      .collect(Collectors.toList());
  }
}
