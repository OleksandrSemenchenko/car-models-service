package ua.com.foxminded.cars.security;

import java.util.Map;

import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import ua.com.foxminded.cars.converter.KeycloakJwtGrantedAuthoritiesConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    public static final String  KEYCLOAK_CREDENTIALS_KEY = "secret";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String EDITOR_ROLE = "EDITOR";
    
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;
    
    @Value("${keycloak.realm}")
    String keycloakRealm;
    
    @Value("${keycloak.auth-server-url}")
    String keycloakAuthServerUrl;
    
    @Value("${keycloak.resource}")
    String keycloakResource;
    
    @Value("${keycloak.credentials.secret}")
    String keycloakCredentialsSecret;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers(HttpMethod.PUT).hasRole(EDITOR_ROLE)
                .requestMatchers(HttpMethod.POST).hasRole(EDITOR_ROLE)
                .requestMatchers(HttpMethod.DELETE).hasRole(ADMIN_ROLE)
                .anyRequest().authenticated())
            .cors(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwkSetUri(jwkSetUri)))
            .addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }
    
    private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
        return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {
            
            @Override
            public PolicyEnforcerConfig resolve(HttpRequest request) {
                PolicyEnforcerConfig config = new PolicyEnforcerConfig();
                config.setRealm(keycloakRealm);
                config.setAuthServerUrl(keycloakAuthServerUrl);
                config.setResource(keycloakResource);
                config.setCredentials(Map.of(KEYCLOAK_CREDENTIALS_KEY, keycloakCredentialsSecret));
                return config;
            }
        });
    }
    
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
    
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
}
