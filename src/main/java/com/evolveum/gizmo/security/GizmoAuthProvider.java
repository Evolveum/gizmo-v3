/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.security;

import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.repository.UserRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @author lazyman
 */
public class GizmoAuthProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoAuthProvider.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${gizmo.authentication.provider}")
    private String providerType;

    @Value("${gizmo.ldap.host}")
    private String ldapHost;
    @Value("${gizmo.ldap.username}")
    private String ldapUsername;
    @Value("${gizmo.ldap.password}")
    private String ldapPassword;
    @Value("${gizmo.ldap.groupSearchBase}")
    private String ldapGroupSearchBase;
    @Value("${gizmo.ldap.groupRoleAttribute}")
    private String ldapGroupRoleAttribute;
    @Value("${gizmo.ldap.groupSearchFilter}")
    private String ldapGroupSearchFilter;
    @Value("${gizmo.ldap.userDnPattern}")
    private String userDnPattern;
    @Value("${gizmo.ldap.group}")
    private String gizmoGroup;

    private SimpleBindAunthenticator ldapBindAuthenticator;

    public void init() {
        if (!useLdapAuth()) {
            return;
        }
        LdapContextSource contextSource = new DefaultSpringSecurityContextSource(ldapHost);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        contextSource.afterPropertiesSet();

        DefaultLdapAuthoritiesPopulator ldapAuthoritiesPopulator =
                new DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase);
        ldapAuthoritiesPopulator.setGroupRoleAttribute(ldapGroupRoleAttribute);
        ldapAuthoritiesPopulator.setGroupSearchFilter(ldapGroupSearchFilter);

        ldapBindAuthenticator = new SimpleBindAunthenticator(contextSource, gizmoGroup);
        ldapBindAuthenticator.setUserDnPatterns(new String[]{userDnPattern});
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (StringUtils.isBlank(principal)) {
            throw new BadCredentialsException("web.security.provider.invalid");
        }

        if (useLdapAuth()) {
            return authenticateUsingLdap(authentication);
        }

        return authenticateUsingDb(authentication);
    }

    private Authentication authenticateUsingDb(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        User user = userRepository.findUserByName(principal);
        if (user == null) {
            throw new BadCredentialsException("web.security.provider.invalid");
        }

        if (user.getPassword() == null || !user.getPassword().equals(GizmoUtils.toSha1(password))) {
            throw new BadCredentialsException("GizmoAuthenticationProvider.userPasswordIncorrect");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("GizmoAuthenticationProvider.userDisabled");
        }

        GizmoPrincipal gizmoPrincipal = new GizmoPrincipal(user);

        LOGGER.debug("User '{}' authenticated ({}), authorities: {}", new Object[]{authentication.getPrincipal(),
                authentication.getClass().getSimpleName(), gizmoPrincipal.getAuthorities()});
        return new UsernamePasswordAuthenticationToken(gizmoPrincipal, null, gizmoPrincipal.getAuthorities());
    }

    private Authentication authenticateUsingLdap(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        DirContextOperations ctx = ldapBindAuthenticator.authenticate(authentication);

        User user = userRepository.findUserByName(principal);
        if (user == null) {
            user = createUser(ctx, principal);
        }


        if (!user.isEnabled()) {
            throw new BadCredentialsException("GizmoAuthenticationProvider.userDisabled");
        }

        GizmoPrincipal gizmoPrincipal = new GizmoPrincipal(user);

        LOGGER.debug("User '{}' authenticated ({}), authorities: {}", new Object[]{authentication.getPrincipal(),
                authentication.getClass().getSimpleName(), gizmoPrincipal.getAuthorities()});
        return new UsernamePasswordAuthenticationToken(gizmoPrincipal, null, gizmoPrincipal.getAuthorities());
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

    private User createUser(DirContextOperations ctx, String name) {
        User user = new User();
        user.setFamilyName(ctx.getStringAttribute("sn"));
        user.setGivenName(ctx.getStringAttribute("givenName"));
        user.setName(name);
        user.setLdapDn(ctx.getNameInNamespace());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    private boolean useLdapAuth() {
        if (providerType == null || providerType.equalsIgnoreCase("ldap")) {
            return true;
        }

        return false;
    }
}
