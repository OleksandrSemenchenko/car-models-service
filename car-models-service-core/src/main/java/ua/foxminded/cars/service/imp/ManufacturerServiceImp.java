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
package ua.foxminded.cars.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.ManufacturerService;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImp implements ManufacturerService {

  private final ManufacturerRepository manufacturerRepository;

  @Override
  public void deleteManufacturer(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      throw new ManufacturerNotFoundException(manufacturerName);
    }
  }

  @Override
  public void createManufacturerIfNeeded(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
      manufacturerRepository.save(manufacturer);
    }
  }
}
