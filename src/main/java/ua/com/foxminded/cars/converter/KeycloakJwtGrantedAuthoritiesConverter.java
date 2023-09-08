package ua.com.foxminded.cars.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    public static final String JWT_CLAIM_NAME = "realm_access";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLES_CLAIM_PROPERTY = "roles";

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(JWT_CLAIM_NAME);
        List<String> roles = new ArrayList<>();
        
        if (realmAccess != null) {
            roles =(List<String>) realmAccess.get(ROLES_CLAIM_PROPERTY);
        }
        
        return roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                             .collect(Collectors.toList());
    }
}
