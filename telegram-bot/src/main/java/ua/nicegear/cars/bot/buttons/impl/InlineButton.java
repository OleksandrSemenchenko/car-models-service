package ua.nicegear.cars.bot.buttons.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InlineButton extends Button {

  private InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().build();
  private LinkedHashMap<Object, Object> buttonAttributes;

  public InlineButtonButton(String name, String callbackMessage) {
    this.buttonAttributes = new LinkedHashMap<>();
    this.buttonAttributes.put(name, callbackMessage);
  }
  public InlineButton(LinkedHashMap<Object, Object> buttonAttributes) {
    super(buttonAttributes);
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

  private InlineKeyboardMarkup addRowToMarkup(InlineKeyboardRow row) {
    List<InlineKeyboardRow> rows = markup.getKeyboard();
    rows.add(row);
    markup.setKeyboard(rows);
    return markup;
  }
}
