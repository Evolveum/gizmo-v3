package sk.lazyman.gizmo.security;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControlExtractor;
import org.springframework.util.Assert;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

/**
 * @author lazyman
 */
public class SimpleBindAunthenticator extends AbstractLdapAuthenticator {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleBindAunthenticator.class);

    private String GROUP_SEARCH_QUERY = "(&(%s)(member=%s))";

    private String gizmoGroup;

    public SimpleBindAunthenticator(BaseLdapPathContextSource contextSource, String gizmoGroup) {
        super(contextSource);
        this.gizmoGroup = gizmoGroup;
    }

    @Override
    public DirContextOperations authenticate(Authentication authentication) {
        DirContextOperations user = null;
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                "Can only process UsernamePasswordAuthenticationToken objects");

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (StringUtils.isEmpty(password)) {
            LOG.debug("Rejecting empty password for user " + username);
            throw new BadCredentialsException(messages.getMessage("BindAuthenticator.emptyPassword",
                    "Empty Password"));
        }

        // If DN patterns are configured, try authenticating with them directly
        for (String dn : getUserDns(username)) {
            user = bindWithDn(dn, username, password);

            if (user != null) {
                break;
            }
        }

        // Otherwise use the configured search object to find the user and authenticate with the returned DN.
        if (user == null && getUserSearch() != null) {
            DirContextOperations userFromSearch = getUserSearch().searchForUser(username);
            user = bindWithDn(userFromSearch.getDn().toString(), username, password);
        }

        try {
            if (user != null && StringUtils.isNotEmpty(gizmoGroup)) {
                BaseLdapPathContextSource ctxSource = (BaseLdapPathContextSource) getContextSource();
                DirContext ctx = ctxSource.getReadOnlyContext();

                DistinguishedName userDn = new DistinguishedName(user.getDn());
                userDn.prepend(ctxSource.getBaseLdapPath());

                SearchControls controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                String filter = String.format(GROUP_SEARCH_QUERY, gizmoGroup, userDn.toCompactString());
                NamingEnumeration en = ctx.search("", filter, controls);
                if (!en.hasMore()) {
                    throw new BadCredentialsException(
                            messages.getMessage("BindAuthenticator.badCredentials", "Bad credentials"));
                }
            }
        } catch (javax.naming.NamingException ex) {
            throw new BadCredentialsException("Couldn't check group membership");
        }

        if (user == null) {
            throw new BadCredentialsException(
                    messages.getMessage("BindAuthenticator.badCredentials", "Bad credentials"));
        }

        return user;
    }

    private DirContextOperations bindWithDn(String userDnStr, String username, String password) {
        BaseLdapPathContextSource ctxSource = (BaseLdapPathContextSource) getContextSource();
        DistinguishedName userDn = new DistinguishedName(userDnStr);
        DistinguishedName fullDn = new DistinguishedName(userDn);
        fullDn.prepend(ctxSource.getBaseLdapPath());

        LOG.debug("Attempting to bind as " + fullDn);

        DirContext ctx = null;
        try {
            ctx = getContextSource().getContext(fullDn.toString(), password);
            // Check for password policy control
            PasswordPolicyControl ppolicy = PasswordPolicyControlExtractor.extractControl(ctx);

            LOG.debug("Retrieving attributes...");
            DirContext readOnlyCtx = getContextSource().getReadOnlyContext();
            Attributes attrs = readOnlyCtx.getAttributes(userDn, getUserAttributes());

            DirContextAdapter result = new DirContextAdapter(attrs, userDn, ctxSource.getBaseLdapPath());
            if (ppolicy != null) {
                result.setAttributeValue(ppolicy.getID(), ppolicy);
            }

            return result;
        } catch (NamingException e) {
            // This will be thrown if an invalid user name is used and the method may
            // be called multiple times to try different names, so we trap the exception
            // unless a subclass wishes to implement more specialized behaviour.
            if ((e instanceof org.springframework.ldap.AuthenticationException)
                    || (e instanceof org.springframework.ldap.OperationNotSupportedException)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failed to bind as " + userDn + ": " + e);
                }
            } else {
                throw e;
            }
        } catch (javax.naming.NamingException e) {
            throw LdapUtils.convertLdapException(e);
        } finally {
            LdapUtils.closeContext(ctx);
        }

        return null;
    }
}
