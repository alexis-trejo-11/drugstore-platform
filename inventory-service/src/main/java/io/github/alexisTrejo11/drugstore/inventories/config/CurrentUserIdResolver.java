package io.github.alexisTrejo11.drugstore.inventories.config;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import libs_kernel.security.dto.AuthUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserIdResolver {

    private CurrentUserIdResolver() {
    }

    /**
     * Returns the authenticated user's id, or {@code system} when there is no JWT-backed principal.
     */
    public static UserId currentUserIdOrSystem() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUserDetails details) {
            String id = details.getUserId();
            if (id != null && !id.isBlank()) {
                return UserId.of(id);
            }
        }
        return UserId.of("system");
    }
}
