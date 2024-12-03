package ua.nicegear.cars.bot.service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.mapper.FilterMapper;
import ua.nicegear.cars.bot.repository.FilterRepository;
import ua.nicegear.cars.bot.repository.entity.Filter;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

  private final FilterRepository filterRepository;
  private final FilterMapper filterMapper;

  private ConcurrentMap<Long, FilterDto> cache = new ConcurrentHashMap<>();

  @Override
  public FilterDto getFiltersByChatId(long chatId) {
    FilterDto cachedFilterDto = cache.get(chatId);

    if (Objects.nonNull(cachedFilterDto)) {
      return cachedFilterDto;
    }
    Filter filter = filterRepository.findByChatId(chatId);
    FilterDto filterDto = filterMapper.toDto(filter);
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
  public FilterDto persistCache(long chatId) {
    FilterDto filterDto = cache.get(chatId);
    Filter entity = filterMapper.toEntity(filterDto);
    Filter savedEntity = filterRepository.save(entity);
    return filterMapper.toDto(savedEntity);
  }
}
