/*
 *  Copyright (C) 2025 Evolveum
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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class GizmoOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;

    public GizmoOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        String username = oidcUser.getPreferredUsername();

        User gizmoUser = userRepository.findUserByName(username);
        if (gizmoUser == null) {
            gizmoUser = new User();
            gizmoUser.setName(username);
            gizmoUser.setGivenName(oidcUser.getGivenName());
            gizmoUser.setFamilyName(oidcUser.getFamilyName());
            userRepository.save(gizmoUser);
        }

        return new GizmoPrincipal(gizmoUser, oidcUser.getAttributes(),
                oidcUser.getIdToken(), oidcUser.getUserInfo(), oidcUser.getClaims());
    }
}
