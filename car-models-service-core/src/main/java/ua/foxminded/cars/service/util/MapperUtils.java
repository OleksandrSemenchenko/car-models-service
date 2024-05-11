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
package ua.foxminded.cars.service.util;

import static java.util.Objects.isNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public class MapperUtils {

  public static String[] definePropertiesWithNullValued(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();
    List<String> fieldNames = new ArrayList<>();

    for (Field field : fields) {
      field.setAccessible(true);
      Object fieldValue = ReflectionUtils.getField(field, object);

      if (isNull(fieldValue)) {
        fieldNames.add(field.getName());
      }
    }
    return fieldNames.toArray(new String[0]);
  }
}
