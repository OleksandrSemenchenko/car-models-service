package ua.nicegear.cars.bot.commands;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

@RequiredArgsConstructor
public abstract class AbstractCommand {

  private final FilterService filterService;
  private final long chatId;

  protected void updateCache(BodyStyle bodyStyle) {
    FilterDto filterDto = FilterDto.builder().chatId(chatId).bodyStyles(Set.of(bodyStyle)).build();
    filterService.updateCacheByNotNullValues(filterDto);
  }
}
