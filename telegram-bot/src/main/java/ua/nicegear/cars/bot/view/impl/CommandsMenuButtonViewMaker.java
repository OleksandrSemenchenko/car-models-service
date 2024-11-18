package ua.nicegear.cars.bot.view.impl;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import ua.nicegear.cars.bot.commands.Command;
import ua.nicegear.cars.bot.commands.CommandMaker;
import ua.nicegear.cars.bot.commands.StopCommand;
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
    Command stop = new StopCommand(commandsConfig);
    CommandMaker stopCommandMaker = new CommandMaker(stop);
    stopCommandMaker.addCommandTo(myCommands);
    return myCommands;
  }

  private SetChatMenuButton createMenuButtonCommands(long chatId) {
    return SetChatMenuButton.builder().menuButton(new MenuButtonCommands()).chatId(chatId).build();
  }
}
