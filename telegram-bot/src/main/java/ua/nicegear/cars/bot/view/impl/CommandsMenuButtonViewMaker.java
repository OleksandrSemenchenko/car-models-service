package ua.nicegear.cars.bot.view.impl;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import ua.nicegear.cars.bot.commands.MenuCommand;
import ua.nicegear.cars.bot.commands.MenuCommandMaker;
import ua.nicegear.cars.bot.commands.impl.StopMenuCommand;
import ua.nicegear.cars.bot.config.CommandsConfig;
import ua.nicegear.cars.bot.view.MenuButtonView;
import ua.nicegear.cars.bot.view.MenuButtonViewMaker;

@RequiredArgsConstructor
public class CommandsMenuButtonViewMaker extends MenuButtonViewMaker {

  private CommandsConfig commandsConfig;
  private SetMyCommands myCommands;

  public CommandsMenuButtonViewMaker(CommandsConfig commandsConfig) {
    this.commandsConfig = commandsConfig;
    this.myCommands = new SetMyCommands(new ArrayList<>());
  }

  @Override
  public MenuButtonView makeView(long chatId) {
    SetMyCommands commands = defineCommands();
    SetChatMenuButton menuButton = createMenuButtonCommands(chatId);
    return new MenuButtonView(commands, menuButton);
  }

  private SetMyCommands defineCommands() {
    myCommands = createStopCommandAndAddTo(myCommands);
    return myCommands;
  }

  private SetMyCommands createStopCommandAndAddTo(SetMyCommands myCommands) {
    MenuCommand stop = new StopMenuCommand(commandsConfig);
    MenuCommandMaker stopMenuCommandMaker = new MenuCommandMaker(stop);
    stopMenuCommandMaker.addCommandTo(myCommands);
    return myCommands;
  }

  private SetChatMenuButton createMenuButtonCommands(long chatId) {
    return SetChatMenuButton.builder().menuButton(new MenuButtonCommands()).chatId(chatId).build();
  }
}
