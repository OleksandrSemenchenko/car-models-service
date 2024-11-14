package ua.nicegear.cars.bot.service;

import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Service
public class FilterServiceImpl implements FilterService {

  @Override
  public FilterDto getFilterByUserId(long userId) {
    return FilterDto.builder()
        .userId(1L)
        .minYear(2017)
        .maxYear(2024)
        .maxMileage(140000)
        .numberOfOwners(1)
        .bodyStyle(BodyStyle.HATCHBACK)
        .build();
  }
}
