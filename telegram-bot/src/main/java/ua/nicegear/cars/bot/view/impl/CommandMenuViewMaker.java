package ua.nicegear.cars.bot.view.impl;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import ua.nicegear.cars.bot.view.ButtonViewMaker;

@RequiredArgsConstructor
public class CommandMenuViewMaker extends ButtonViewMaker<SetChatMenuButton> {

  private final long chartId;

  @Override
  public SetChatMenuButton makeView(SetChatMenuButton setChatMenuButton) {
    setChatMenuButton.setChatId(chartId);
    setChatMenuButton.setMenuButton(new MenuButtonCommands());
    return setChatMenuButton;
  }
}
