package ua.nicegear.cars.bot.service;

import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Service
public class SearchFilterServiceImpl implements SearchFilterService {

  @Override
  public SearchFilterDto getSearchFilterByChatId(long userId) {
    return SearchFilterDto.builder()
        .userId(1L)
        .minYear(2017)
        .maxYear(2024)
        .maxMileage(140000)
        .numberOfOwners(1)
        .bodyStyle(BodyStyle.HATCHBACK)
        .build();
  }
}
