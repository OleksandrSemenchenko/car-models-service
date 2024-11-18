package ua.nicegear.cars.bot.commands;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@RequiredArgsConstructor
public class CommandMaker {

  private final Command command;

  public SetMyCommands addCommandTo(SetMyCommands myCommands) {
    BotCommand botCommand = buildCommand();
    List<BotCommand> commands = myCommands.getCommands();
    commands.add(botCommand);
    return myCommands;
  }

  private BotCommand buildCommand() {
    return BotCommand.builder()
        .command(command.getName())
        .description(command.getDescription())
        .build();
  }
}
