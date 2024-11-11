package ua.nicegear.cars.bot.buttons;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Setter
public class ResponseContext {

  private InlineKeyboardAbstractButton button;

  public SendMessage addInlineButtonTo(SendMessage sendMessage) {
    return button.addButtonTo(sendMessage);
  }
}
