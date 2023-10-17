package ua.com.foxminded.converter;

import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.PathConfig;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class PathConfigConverter implements Converter<String, PathConfig> {

    @Override
    public PathConfig convert(String source) {
        PathConfig path = new PathConfig();
        path.setPath(source);
        return path;
    }
}
