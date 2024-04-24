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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.dto.ModelNameDto;
import ua.com.foxminded.entity.ModelName;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNameAlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNameNotFoundException;
import ua.com.foxminded.mapper.ModelNameMapper;
import ua.com.foxminded.repository.ModelNameRepository;

@Service
@RequiredArgsConstructor
public class ModelNameService {
  
  private static final String MODEL_NAME_CACHE = "modelNames";

  private final ModelNameRepository modelNameRepository;
  private final ModelNameMapper modeNamelMapper;

  @Transactional
  @CachePut(value = MODEL_NAME_CACHE, key = "{ 'getByName', #modelNameDto.name }")
  @CacheEvict(value = MODEL_NAME_CACHE, key = "'getAll'")
  public ModelNameDto create(ModelNameDto modelNameDto) {
    if (modelNameRepository.existsById(modelNameDto.getName())) {
      throw new ModelNameAlreadyExistsException(modelNameDto.getName());
    }

    ModelName modelName = modeNamelMapper.map(modelNameDto);
    ModelName persistedModelName = modelNameRepository.save(modelName);
    return modeNamelMapper.map(persistedModelName);
  }

  @Cacheable(value = MODEL_NAME_CACHE, key = "#root.methodName")
  public Page<ModelNameDto> getAll(Pageable pageable) {
    return modelNameRepository.findAll(pageable).map(modeNamelMapper::map);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = MODEL_NAME_CACHE, key = "{ 'getByName', #name }"),
      @CacheEvict(value = MODEL_NAME_CACHE, key = "'getAll'")
      
  })
  public void deleteByName(String name) {
    modelNameRepository
        .findById(name)
        .orElseThrow(() -> new ModelNameNotFoundException(name));
    modelNameRepository.deleteById(name);
  }

  @Cacheable(value = MODEL_NAME_CACHE, key = "{ #root.methodName, #name }")
  public ModelNameDto getByName(String name) {
    return modelNameRepository
        .findById(name)
        .map(modeNamelMapper::map)
        .orElseThrow(() -> new ModelNameNotFoundException(name));
  }
}
