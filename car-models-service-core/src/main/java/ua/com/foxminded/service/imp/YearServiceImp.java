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
package ua.com.foxminded.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.foxminded.exceptionhandler.exceptions.YearNotFoundException;
import ua.com.foxminded.repository.YearRepository;
import ua.com.foxminded.repository.entity.Year;
import ua.com.foxminded.service.YearService;

@Service
@RequiredArgsConstructor
public class YearServiceImp implements YearService {

  private final YearRepository yearRepository;

  @Override
  public void deleteYear(int value) {
    if (!yearRepository.existsById(value)) {
      throw new YearNotFoundException(value);
    }
    yearRepository.deleteById(value);
  }

  @Override
  public void createYearIfNeeded(int value) {
    if (!yearRepository.existsById(value)) {
      Year year = Year.builder().value(value).build();
      yearRepository.save(year);
    }
  }
}
