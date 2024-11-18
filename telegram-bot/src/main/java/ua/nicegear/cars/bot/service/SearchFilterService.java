package ua.nicegear.cars.bot.service;

import ua.nicegear.cars.bot.dto.SearchFilterDto;

public interface SearchFilterService {

  SearchFilterDto getSearchFilterByChatId(long chatId);
}
