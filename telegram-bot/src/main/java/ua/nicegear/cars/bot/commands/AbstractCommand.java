package ua.nicegear.cars.bot.commands;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.impl.BodyStyleStrategy;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

@RequiredArgsConstructor
public abstract class AbstractCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  protected ConsumeStrategy getBodyStyleStrategy(Set<BodyStyle> bodyStyles) {
    FilterDto filterDto = FilterDto.builder().bodyStyles(bodyStyles).build();
    filterService.updateCache(filterDto);
    return new BodyStyleStrategy(telegramClient, buttonsConfig, filterService);
  }
}
