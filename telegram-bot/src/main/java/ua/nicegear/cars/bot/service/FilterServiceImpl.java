package ua.nicegear.cars.bot.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.mapper.FilterMapper;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

  private final FilterMapper filterMapper;
  private ConcurrentMap<Long, FilterDto> cache = new ConcurrentHashMap<>();

  @Override
  public FilterDto getFiltersByChatId(long chatId) {
    FilterDto cachedFilterDto = cache.get(chatId);

    if (Objects.nonNull(cachedFilterDto)) {
      return cachedFilterDto;
    }
    FilterDto filterDto =
        FilterDto.builder()
            .chatId(chatId)
            .minYear(2017)
            .maxYear(2024)
            .maxMileage(140000)
            .numberOfOwners(1)
            .bodyStyles(new HashSet<>(List.of(BodyStyle.HATCHBACK)))
            .build();
    return saveToCache(filterDto);
  }

  @Override
  public FilterDto updateCacheByNotNullValues(FilterDto filterDto) {
    FilterDto cachedFilterDto = cache.get(filterDto.getChatId());

    if (Objects.nonNull(cachedFilterDto)) {
      filterMapper.updateByNotNullValues(filterDto, cachedFilterDto);
      cache.put(filterDto.getChatId(), cachedFilterDto);
      return cachedFilterDto;
    } else {
      cache.put(filterDto.getChatId(), filterDto);
      return filterDto;
    }
  }

  @Override
  public FilterDto saveToCache(FilterDto filterDto) {
    cache.put(filterDto.getChatId(), filterDto);
    return filterDto;
  }

  @Override
  public void persistCache(long chatId) {
    // TODO save cache to db

  }
}
