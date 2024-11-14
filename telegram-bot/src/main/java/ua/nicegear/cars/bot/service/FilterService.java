package ua.nicegear.cars.bot.service;

import ua.nicegear.cars.bot.dto.FilterDto;

public interface FilterService {

  FilterDto getFilterByUserId(long userId);
}
