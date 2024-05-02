/*
 * Copyright 2023 Oleksandr Semenchenko
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
package ua.com.foxminded.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.service.dto.ModelDto;

@Mapper(
    nullValueCheckStrategy = ALWAYS,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {CategoryMapper.class, ModelNameMapper.class, ManufacturerMapper.class})
public interface ModelMapper {

  @Mapping(target = "name", source = "modelName")
  ModelDto map(Model model);

  @InheritInverseConfiguration
  Model map(ModelDto modelDto);
}
