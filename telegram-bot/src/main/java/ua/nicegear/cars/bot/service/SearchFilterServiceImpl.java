package ua.nicegear.cars.bot.service;

import java.util.Objects;
import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.SearchFilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Service
public class SearchFilterServiceImpl implements SearchFilterService {

  private SearchFilterDto searchFilterDtoCache;

  @Override
  public SearchFilterDto getSearchFilterByChatId(long userId) {
    if (Objects.nonNull(searchFilterDtoCache)) {
      return searchFilterDtoCache;
    }
    SearchFilterDto searchFilterDto =
        SearchFilterDto.builder()
            .userId(1L)
            .minYear(2017)
            .maxYear(2024)
            .maxMileage(140000)
            .numberOfOwners(1)
            .bodyStyle(BodyStyle.HATCHBACK)
            .build();
    searchFilterDtoCache = searchFilterDto;
    return searchFilterDto;
  }

  @Override
  public SearchFilterDto saveToCache(SearchFilterDto searchFilterDto) {

    return null;
  }
}
