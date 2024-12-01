package ua.nicegear.cars.bot.commands.menu;

import ua.nicegear.cars.bot.config.CommandsConfig;

public class StopMenuCommand extends MenuCommand {

  private String name;
  private String description;

  public StopMenuCommand(CommandsConfig commandConfig) {
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
