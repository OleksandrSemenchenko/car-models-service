package ua.nicegear.cars.bot.commands;

import ua.nicegear.cars.bot.config.CommandsConfig;

public class StopCommand extends Command {

  private String name;
  private String description;

  public StopCommand(CommandsConfig commandConfig) {
    this.name = commandConfig.getStopName();
    this.description = commandConfig.getStopDescription();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
