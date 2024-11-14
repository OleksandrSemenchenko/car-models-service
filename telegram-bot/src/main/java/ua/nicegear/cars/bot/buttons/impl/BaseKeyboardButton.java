package ua.nicegear.cars.bot.buttons.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

public class BaseKeyboardButton extends Button {

  private ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new ArrayList<>());
  private List<String> buttonNames;

  public BaseKeyboardButton(List<String> buttonNames) {
    this.buttonNames = buttonNames;
  }

  @Override
  protected SendMessage addButtonTo(SendMessage sendMessage) {
    var buttons = buildButton();
    var row = new KeyboardRow(buttons);
    markup = addRowToMarkup(row);
    sendMessage.setReplyMarkup(markup);
    return sendMessage;
  }

  private List<KeyboardButton> buildButton() {
    return buttonNames.stream()
        .map(name -> KeyboardButton.builder().text(name).build())
        .collect(Collectors.toList());
  }

  private ReplyKeyboardMarkup addRowToMarkup(KeyboardRow row) {
    List<KeyboardRow> rows = markup.getKeyboard();
    rows.add(row);
    markup.setKeyboard(rows);
    markup.setResizeKeyboard(true);
    return markup;
  }
}
