package ua.com.foxminded.cars.security;

import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
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
                return policyEnforcerConfig();
            }
        });
    }
    
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
    
    @Bean
    @ConfigurationProperties("keycloak.policy-enforcer-config")
    PolicyEnforcerConfig policyEnforcerConfig() {
        return new PolicyEnforcerConfig();
    }
}
