package no.nav.infotrygd.foreldrepenger.sikkerhet;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class EntraJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    static final String SYSTEM = "SYSTEM";

    private static final String CLAIM_ROLES = "roles";
    private static final String ROLE_CLAIM_VALUE_ACCESS_AS_APPLICATION = "access_as_application";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt, authorities(jwt));
    }

    private static List<GrantedAuthority> authorities(Jwt jwt) {
        var roller = jwt.getClaimAsStringList(CLAIM_ROLES);
        if (roller != null && roller.contains(ROLE_CLAIM_VALUE_ACCESS_AS_APPLICATION)) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + SYSTEM));
        }
        return List.of();
    }
}
