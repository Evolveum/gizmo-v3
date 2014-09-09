package sk.lazyman.gizmo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author lazyman
 */
public class SecurityUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

    public static GizmoPrincipal getPrincipalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return getPrincipalUser(authentication);
    }

    public static GizmoPrincipal getPrincipalUser(Authentication authentication) {
        if (authentication == null) {
            LOGGER.debug("Authentication not available in security current context holder.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof GizmoPrincipal)) {
            LOGGER.debug("Principal user in security context holder is {} but not type of {}",
                    new Object[]{principal, GizmoPrincipal.class.getName()});
            return null;
        }

        return (GizmoPrincipal) principal;
    }
}
