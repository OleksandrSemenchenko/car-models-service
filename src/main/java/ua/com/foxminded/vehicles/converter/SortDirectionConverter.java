package ua.com.foxminded.vehicles.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class SortDirectionConverter implements Converter<String, Direction> {

    @Override
    public Direction convert(String source) {
        return Direction.valueOf(source);
    }
}
