package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.service.dto.ModelDto;
import ua.foxminded.cars.service.util.MapperUtils;

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

  // TODO
  default Model mergeWithDto(ModelDto modelDto, Model model) {
    BeanUtils.copyProperties(this.toEntity(modelDto), model);
    return model;
  }

  // TODO
  default Model mergeWithNotNullDtoProperties(ModelDto modelDto, Model model) {
    String[] notNullProperties = MapperUtils.defineNullProperties(modelDto);
    BeanUtils.copyProperties(this.toEntity(modelDto), model, notNullProperties);
    return model;
  }
}
