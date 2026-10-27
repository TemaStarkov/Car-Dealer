package org.example.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class CurrentUserService {

    public UUID getCurrentUserId() {
        Jwt jwt = getJwt();
        return UUID.fromString(jwt.getSubject());
    }

    public String getUsername() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("preferred_username");
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasAnyRole(String... roles) {
        return Arrays.stream(roles).anyMatch(this::hasRole);
    }

    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("User is not authenticated");
        }

        return jwt;
    }
}