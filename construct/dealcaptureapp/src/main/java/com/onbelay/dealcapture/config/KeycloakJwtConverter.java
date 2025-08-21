package com.onbelay.dealcapture.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger logger = LogManager.getLogger();
    private final String applicationName;
    private boolean hasRealmAccess = false;

    public KeycloakJwtConverter(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        ArrayList<String> roleNames = new ArrayList<>();

        if (hasRealmAccess) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            roleNames.addAll((List<String>) realmAccess.get("roles"));
        }

        Map<String, Object> resourceAccess  = jwt.getClaimAsMap("resource_access");
        Map<String, Object> dealCaptureMap = (Map<String, Object>) resourceAccess.get(applicationName);
        roleNames.addAll((List<String>) dealCaptureMap.get("roles"));

        List<GrantedAuthority> authorities =  roleNames
                .stream()
                .distinct()
                .map(role -> (GrantedAuthority)new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())).toList();
        return authorities;
    }
}
