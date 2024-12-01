package ua.nicegear.cars.bot.service;

import ua.nicegear.cars.bot.dto.FilterDto;

public interface FilterService {

  FilterDto getSearchFilterByChatId(long chatId);

  FilterDto updateCache(FilterDto filterDto);
}
