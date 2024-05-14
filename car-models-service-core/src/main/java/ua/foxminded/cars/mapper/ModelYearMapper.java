package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.time.Year;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.cars.repository.entity.ModelYear;

@Mapper(componentModel = SPRING)
public interface ModelYearMapper {

  default Integer toInteger(ModelYear modelYear) {
    Year year = modelYear.getValue();
    return year.getValue();
  }

  @Mapping(target = "value", source = "year")
  ModelYear toEntity(Integer year);

  default Year toYear(Integer year) {
    return Year.of(year);
  }
}
