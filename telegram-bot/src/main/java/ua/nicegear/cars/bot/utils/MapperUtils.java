package ua.nicegear.cars.bot.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.util.ReflectionUtils;

public class MapperUtils {

  public static String[] defineNullProperties(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();
    List<String> fieldNames = new ArrayList<>();

    for (Field field : fields) {
      field.setAccessible(true);
      Object fieldValue = ReflectionUtils.getField(field, object);

      if (Objects.isNull(fieldValue)) {
        fieldNames.add(field.getName());
      }
    }
    return fieldNames.toArray(new String[0]);
  }
}
