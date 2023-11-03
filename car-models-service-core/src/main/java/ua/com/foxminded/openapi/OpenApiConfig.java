package ua.com.foxminded.openapi;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Car models service API", 
                                version = "1.0.1", 
                                description = "The API to persist and search car models in a database"))
@SecurityScheme(
name = "bearerAuth",
type = SecuritySchemeType.HTTP,
bearerFormat = "JWT",
scheme = "bearer")
public class OpenApiConfig {

}
