package ua.nicegear.cars.bot.buttons;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public abstract class InlineKeyboardAbstractButton {

  protected InlineKeyboardMarkup markup;

  public InlineKeyboardAbstractButton() {
    markup = InlineKeyboardMarkup.builder().build();
  }

  protected abstract SendMessage addButtonTo(SendMessage sendMessage);

  protected InlineKeyboardMarkup addRowToMarkup(InlineKeyboardRow row) {
    List<InlineKeyboardRow> rows = markup.getKeyboard();
    rows.add(row);
    markup.setKeyboard(rows);
    return markup;
  }
}
