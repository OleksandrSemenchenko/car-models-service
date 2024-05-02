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

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.repository.entity.ModelName;
import ua.com.foxminded.service.dto.ModelNameDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelNameMapper {

  ModelNameDto map(ModelName modelName);

  @InheritInverseConfiguration
  @Mapping(target = "models", ignore = true)
  ModelName map(ModelNameDto modelNameDto);

  default String modelNameToString(ModelName modelName) {
    return modelName.getName();
  }

  default ModelName stringToModelName(String name) {
    return ModelName.builder().name(name).build();
  }
}
