package sk.lazyman.gizmo.security;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

/**
 * @author lazyman
 */
public class GizmoAuthWebSession extends AuthenticatedWebSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoAuthWebSession.class);

    @SpringBean(name = "gizmoAuthenticationProvider")
    private AuthenticationProvider authenticationProvider;

    public GizmoAuthWebSession(Request request) {
        super(request);
        Injector.get().inject(this);

        if (getLocale() == null) {
            //default locale for web application
            setLocale(new Locale("en", "US"));
        }
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        //todo - used for wicket auth roles...
        return roles;
    }

    public static GizmoAuthWebSession getSession() {
        return (GizmoAuthWebSession) Session.get();
    }

    @Override
    public boolean authenticate(String username, String password) {
        LOGGER.debug("Authenticating '{}' {} password in web session.",
                new Object[]{username, (StringUtils.isEmpty(password) ? "without" : "with")});

        boolean authenticated;
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authenticated = authentication.isAuthenticated();
        } catch (AuthenticationException ex) {
            ComponentStringResourceLoader comp = new ComponentStringResourceLoader();
            error(comp.loadStringResource(GizmoApplication.class, ex.getMessage(), getLocale(), "", ""));

            LOGGER.debug("Couldn't authenticate user.", ex);
            authenticated = false;
        }

        return authenticated;
    }
}
