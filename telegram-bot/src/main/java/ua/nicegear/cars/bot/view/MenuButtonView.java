package ua.nicegear.cars.bot.view;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;

@Data
@RequiredArgsConstructor
public class MenuButtonView {

  private final SetMyCommands myCommands;
  private final SetChatMenuButton chatMenuButton;
}
