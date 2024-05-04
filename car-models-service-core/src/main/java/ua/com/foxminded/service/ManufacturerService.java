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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.exceptionhandler.exceptions.ManufacturerAlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.com.foxminded.mapper.ManufacturerMapper;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.service.dto.ManufacturerDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {

  private static final String MANUFACTURERS_CACHE = "manufacturers";

  private final ManufacturerRepository manufacturerRepository;
  private final ManufacturerMapper manufacturerMapper;

  @Transactional
  @CachePut(value = MANUFACTURERS_CACHE, key = "{ 'getByName', #manufacturerDto.name }")
  @CacheEvict(value = MANUFACTURERS_CACHE, key = "'getAll'")
  public ManufacturerDto create(ManufacturerDto manufacturerDto) {
    verifyIfManufacturerExists(manufacturerDto.getName());
    Manufacturer manufacturer = manufacturerMapper.toDto(manufacturerDto);
    Manufacturer persistedManufacturer = manufacturerRepository.save(manufacturer);
    return manufacturerMapper.toEntity(persistedManufacturer);
  }

  private void verifyIfManufacturerExists(String name) {
    if (manufacturerRepository.existsById(name)) {
      throw new ManufacturerAlreadyExistsException(name);
    }
  }

  @Cacheable(value = MANUFACTURERS_CACHE, key = "#root.methodName")
  public Page<ManufacturerDto> getAll(Pageable pageable) {
    return manufacturerRepository.findAll(pageable).map(manufacturerMapper::toEntity);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = MANUFACTURERS_CACHE, key = "{ 'getByName', #name }"),
        @CacheEvict(value = MANUFACTURERS_CACHE, key = "'getAll'")
      })
  public void deleteByName(String name) {
    manufacturerRepository
        .findById(name)
        .orElseThrow(() -> new ManufacturerNotFoundException(name));
    manufacturerRepository.deleteById(name);
  }

  @Cacheable(value = MANUFACTURERS_CACHE, key = "{ #root.methodName, #name }")
  public ManufacturerDto getByName(String name) {
    return manufacturerRepository
        .findById(name)
        .map(manufacturerMapper::toEntity)
        .orElseThrow(() -> new ManufacturerNotFoundException(name));
  }
}
