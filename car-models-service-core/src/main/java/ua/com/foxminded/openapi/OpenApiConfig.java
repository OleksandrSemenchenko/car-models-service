package ua.com.foxminded.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Car models service API", 
                                version = "${build.version}", 
                                description = "The API to manage car models"), 
                   servers = @Server(url = "http://localhost:${server.port}/v${build.majorVersion}", 
                                     description = "Development server"))
@SecurityScheme(
name = "bearerAuth",
type = SecuritySchemeType.HTTP,
bearerFormat = "JWT",
scheme = "bearer")
public class OpenApiConfig {

}
