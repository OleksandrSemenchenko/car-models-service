package ua.foxminded.cars.config;

import static java.util.Objects.nonNull;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class PolicyEnforcerFilter implements Filter {

  private final PolicyEnforcerConfig policyEnforcerConfig;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (isBearerTokenPresent(httpRequest) || nonNull(authentication)) {
      createPolicyEnforcerFilter();
    }
    chain.doFilter(request, response);
  }

  private boolean isBearerTokenPresent(HttpServletRequest request) {
    Enumeration<String> authorizationHeaderValues = request.getHeaders("Authorization");
    return authorizationHeaderValues.hasMoreElements();
  }

  private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
    return new ServletPolicyEnforcerFilter(
        new ConfigurationResolver() {

          @Override
          public PolicyEnforcerConfig resolve(HttpRequest request) {
            return policyEnforcerConfig;
          }
        });
  }
}
