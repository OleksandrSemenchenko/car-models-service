package ua.nicegear.cars.bot.buttons.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ua.nicegear.cars.bot.buttons.InlineKeyboardAbstractButton;
import ua.nicegear.cars.bot.constants.CallbackMessage;

public class DetailsInlineKeyboardButton extends InlineKeyboardAbstractButton {

  @Override
  public SendMessage addButtonTo(SendMessage sendMessage) {
    var button = buildDetailsButton();
    var row = new InlineKeyboardRow(button);
    markup = addRowToMarkup(row);
    sendMessage.setReplyMarkup(markup);
    return sendMessage;
  }

  //TODO move strings to application.yaml
  private InlineKeyboardButton buildDetailsButton() {
    return InlineKeyboardButton.builder()
        .text("Detailed information")
        .callbackData(CallbackMessage.DETAILS_BUTTON)
        .build();
  }
}
