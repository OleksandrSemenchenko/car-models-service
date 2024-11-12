package ua.nicegear.cars.bot.buttons;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Setter
public class ButtonMaker {

  private Button button;

  public SendMessage makeButtonFor(SendMessage sendMessage) {
    return button.addButtonTo(sendMessage);
  }
}
