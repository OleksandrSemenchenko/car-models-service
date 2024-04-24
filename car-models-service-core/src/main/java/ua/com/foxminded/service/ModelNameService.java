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
package ua.com.foxminded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.dto.ModelNameDto;
import ua.com.foxminded.entity.ModelName;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.NotFoundException;
import ua.com.foxminded.mapper.ModelNameMapper;
import ua.com.foxminded.repository.ModelNameRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelNameService {

  public static final String NO_MODEL_NAME = "The model name '%s' doesn't exist";
  public static final String MODEL_NAME_ALREADY_EXISTS = "The model name '%s' already exists";

  private final ModelNameRepository modelNameRepository;
  private final ModelNameMapper modeNamelMapper;

  public ModelNameDto create(ModelNameDto modelNameDto) {
    if (modelNameRepository.existsById(modelNameDto.getName())) {
      throw new AlreadyExistsException(
          String.format(MODEL_NAME_ALREADY_EXISTS, modelNameDto.getName()));
    }

    ModelName modelName = modeNamelMapper.map(modelNameDto);
    ModelName persistedModelName = modelNameRepository.save(modelName);
    return modeNamelMapper.map(persistedModelName);
  }

  public Page<ModelNameDto> getAll(Pageable pageable) {
    return modelNameRepository.findAll(pageable).map(modeNamelMapper::map);
  }

  public void deleteByName(String name) {
    modelNameRepository
        .findById(name)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_NAME, name)));
    modelNameRepository.deleteById(name);
  }

  public ModelNameDto getByName(String name) {
    return modelNameRepository
        .findById(name)
        .map(modeNamelMapper::map)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_NAME, name)));
  }
}
