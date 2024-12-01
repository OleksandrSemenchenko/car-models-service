package ua.nicegear.cars.bot.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.mapper.FilterMapper;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

  private final FilterMapper filterMapper;
  private FilterDto filterDtoCache;

  @Override
  public FilterDto getFiltersByChatId(long userId) {
    if (Objects.nonNull(filterDtoCache)) {
      return filterDtoCache;
    }
    FilterDto filterDto =
        FilterDto.builder()
            .userId(1L)
            .minYear(2017)
            .maxYear(2024)
            .maxMileage(140000)
            .numberOfOwners(1)
            .bodyStyles(new HashSet<>(List.of(BodyStyle.HATCHBACK)))
            .build();
    filterDtoCache = filterDto;
    return filterDto;
  }

  @Override
  public FilterDto updateCacheByNotNullValues(FilterDto filterDto) {
    return filterMapper.updateByNotNullValues(filterDto, filterDtoCache);
  }

  @Override
  public void updateCache(FilterDto filterDto) {
    filterDtoCache = filterDto;
  }
}
