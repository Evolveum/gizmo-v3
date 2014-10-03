package sk.lazyman.gizmo.security;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;

/**
 * @author lazyman
 */
public class GizmoAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoAuthenticationProvider.class);

    @Autowired
    private UserRepository userRepository;

    private SimpleBindAunthenticator ldapBindAuthenticator;

    public GizmoAuthenticationProvider(SimpleBindAunthenticator ldapBindAuthenticator) {
        this.ldapBindAuthenticator = ldapBindAuthenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (StringUtils.isBlank((String) authentication.getPrincipal())) {
            throw new BadCredentialsException("web.security.provider.invalid");
        }

        DirContextOperations ctx = ldapBindAuthenticator.authenticate(authentication);

        User user = userRepository.findUserByName((String) authentication.getPrincipal());
        if (user == null) {
            throw new BadCredentialsException("Couldn't find user in gizmo database.");
        }
        GizmoPrincipal principal = new GizmoPrincipal(user);

        LOGGER.debug("User '{}' authenticated ({}), authorities: {}", new Object[]{authentication.getPrincipal(),
                authentication.getClass().getSimpleName(), principal.getAuthorities()});
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        if (UsernamePasswordAuthenticationToken.class.equals(authentication)) {
            return true;
        }
        if (PreAuthenticatedAuthenticationToken.class.equals(authentication)) {
            return true;
        }

        return false;
    }
}
