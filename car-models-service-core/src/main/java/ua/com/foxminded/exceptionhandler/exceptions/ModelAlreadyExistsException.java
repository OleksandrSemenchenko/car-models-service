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
package ua.com.foxminded.exceptionhandler.exceptions;

import java.util.UUID;

public class ModelAlreadyExistsException extends AlreadyExistsException {


  private static final String MESSAGE_BY_ID = "The model with id='%s' already exists";
  private static final String MESSAGE_BY_PARAMETERS =
    "The model with manufacturer '%s', name '%s' and year '%s' already exists, its id='%s'";

  public ModelAlreadyExistsException(String manufacturer, String name, int year, UUID modelId) {
    super(MESSAGE_BY_PARAMETERS.formatted(manufacturer, name, year, String.valueOf(modelId)));
  }

  public ModelAlreadyExistsException(UUID modelId) {
    super(MESSAGE_BY_ID.formatted(String.valueOf(modelId)));
  }
}
