package ua.nicegear.cars.bot.service;

import ua.nicegear.cars.bot.dto.FilterDto;

public interface FilterService {

  FilterDto getFiltersByChatId(long chatId);

  FilterDto updateCacheByNotNullValues(FilterDto filterDto);

  FilterDto saveToCache(FilterDto filterDto);

  void persistCache(long chatId);
}
