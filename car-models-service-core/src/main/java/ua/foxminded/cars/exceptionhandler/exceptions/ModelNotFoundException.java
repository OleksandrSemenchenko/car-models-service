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
package ua.foxminded.cars.exceptionhandler.exceptions;

import java.util.UUID;

public class ModelNotFoundException extends NotFoundException {

  private static final String MODEL_NOT_FOUND_BY_ID = "The model with id=%s doesn't exist";

  private static final String MODEL_NOT_FOUND_BY_MANUFACTURER_AND_NAME_AND_YEAR =
      "The model with manufacturer '%s', name '%s' and year '%s' doesn't exist";

  public ModelNotFoundException(UUID modelId) {
    super(MODEL_NOT_FOUND_BY_ID.formatted(modelId.toString()));
  }

  public ModelNotFoundException(String manufacturer, String name, int year) {
    super(MODEL_NOT_FOUND_BY_MANUFACTURER_AND_NAME_AND_YEAR.formatted(manufacturer, name, year));
  }
}
