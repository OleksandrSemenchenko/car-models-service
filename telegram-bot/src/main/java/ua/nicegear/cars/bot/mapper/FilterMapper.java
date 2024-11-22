package ua.nicegear.cars.bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.BeanUtils;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.utils.MapperUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FilterMapper {

  default FilterDto updateByNotNullValues(FilterDto source, FilterDto target) {
    String[] ignoreProperties = MapperUtils.defineNullProperties(source);
    BeanUtils.copyProperties(source, target, ignoreProperties);
    return target;
  }
}
