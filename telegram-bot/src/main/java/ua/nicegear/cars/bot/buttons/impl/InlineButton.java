package ua.nicegear.cars.bot.buttons.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ua.nicegear.cars.bot.buttons.Button;

@RequiredArgsConstructor
public class InlineButton extends Button {

  private final LinkedHashMap<Object, Object> buttonDetails;
  private final InlineKeyboardMarkup markup;

  @Override
  public SendMessage addButtonTo(SendMessage sendMessage) {
    var buttons = buildButtons();
    var row = new InlineKeyboardRow(buttons);
    markup.getKeyboard().add(row);
    sendMessage.setReplyMarkup(markup);
    return sendMessage;
  }

  private List<InlineKeyboardButton> buildButtons() {
    return buttonDetails.entrySet().stream()
        .map(
            entry ->
                InlineKeyboardButton.builder()
                    .text(String.valueOf(entry.getKey()))
                    .callbackData(String.valueOf(entry.getValue()))
                    .build())
        .collect(Collectors.toList());
  }
}
