package ua.nicegear.cars.bot.buttons.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

import java.util.List;
import java.util.stream.Collectors;

public class FilterKeyboardButton extends Button {

  private ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder().build();
  private List<String> buttonNames;

  public FilterKeyboardButton(List<String> buttonNames) {
    this.buttonNames = buttonNames;
  }

  @Override
  protected SendMessage addButtonTo(SendMessage sendMessage) {


    return null;
  }

  private List<KeyboardButton> buildButtons() {
    return buttonNames.stream().map(name -> KeyboardButton.builder().text(name).build()).collect(Collectors.toList());
  }

  private ReplyKeyboardMarkup addRowToMarkup(KeyboardRow row) {
    List<KeyboardRow> rows = markup.getKeyboard();
    rows.add(row);
    markup.setKeyboard(rows);
    return markup;
  }
}
