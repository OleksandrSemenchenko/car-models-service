package ua.com.foxminded.openapi;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
      name = "OAuth2",
      type = SecuritySchemeType.OAUTH2,
      flows = @OAuthFlows(authorizationCode = @OAuthFlow(
              authorizationUrl = "http://${KEYCLOAK_HOST:localhost}:8080/auth/realms/car-services/protocol/openid-connect/auth",
              tokenUrl = "http://${KEYCLOAK_HOST:localhost}:8080/auth/realms/car-services/protocol/openid-connect/token",
              scopes = {@OAuthScope(name = "POST", description = "Defined by the keycloak policy enforcer setting when using the method POST"), 
                        @OAuthScope(name = "GET", description = "Defined by the keycloak policy enforcer setting when using the method GET"),
                        @OAuthScope(name = "PUT", description = "Defined by the keycloak policy enforcer setting when using the method PUT"), 
                        @OAuthScope(name = "DELETE", description = "Defined by the keycloak policy enforcer setting when using the method DELETE")}
              )))
public class OpenApiConfig {

}
