/*
 * Copyright 2024 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

@Mapper(nullValueCheckStrategy = ALWAYS, componentModel = SPRING, uses = CategoryMapper.class)
public interface ModelMapper {

  @Mapping(target = "name", source = "name")
  @Mapping(target = "year", source = "year.value")
  @Mapping(target = "manufacturer", source = "manufacturer.name")
  ModelDto toDto(Model model);

  @InheritInverseConfiguration
  Model toEntity(ModelDto modelDto);

  default Model mergeWithDto(ModelDto modelDto, Model model) {
    BeanUtils.copyProperties(this.toEntity(modelDto), model);
    return model;
  }

  default Model mergeWithNotNullDtoProperties(ModelDto modelDto, Model model) {
    String[] notNullProperties = MapperUtils.definePropertiesWithNullValued(modelDto);
    BeanUtils.copyProperties(this.toEntity(modelDto), model, notNullProperties);
    return model;
  }
}
