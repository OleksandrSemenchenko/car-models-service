package ua.nicegear.cars.bot.buttons;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public abstract class KeyboardAbstractButton {

  private ReplyKeyboardMarkup markup;

  public KeyboardAbstractButton() {
    markup = ReplyKeyboardMarkup.builder().build();
  }

  protected abstract void addKeyboardButtonTo(SendMessage sendMessage);

  protected ReplyKeyboardMarkup addRowToMarkup(KeyboardRow row) {
    List<KeyboardRow> rows = markup.getKeyboard();
    rows.add(row);
    markup.setKeyboard(rows);
    return markup;
  }
}
