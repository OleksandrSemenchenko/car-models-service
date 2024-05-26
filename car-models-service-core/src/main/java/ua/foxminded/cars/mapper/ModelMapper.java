package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.service.dto.ModelDto;

@Mapper(
    nullValueCheckStrategy = ALWAYS,
    componentModel = SPRING,
    uses = {CategoryMapper.class, ModelYearMapper.class, ManufacturerMapper.class})
public interface ModelMapper {

  @Mapping(target = "year", source = "year")
  @Mapping(target = "manufacturer", source = "manufacturer.name")
  ModelDto toDto(Model model);

  @InheritInverseConfiguration
  Model toEntity(ModelDto modelDto);
}
