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
package com.evolveum.gizmo;

import com.evolveum.gizmo.security.GizmoAuthProvider;
import com.evolveum.gizmo.security.GizmoOidcUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    private final GizmoOidcUserService gizmoOidcUserService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(GizmoOidcUserService gizmoOidcUserService,
                          ClientRegistrationRepository clientRegistrationRepository) {
        this.gizmoOidcUserService = gizmoOidcUserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/j_spring_security_check",
                                "/login",
                                "/error",
                                "/error/*",
                                "/bootstrap",
                                "/wicket/resource/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/fonts/**",
                                "/static/**",
                                "/favicon.ico")
                            .permitAll()
                        .anyRequest()
                            .authenticated());

        http.formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")       // URL form posts to
                .defaultSuccessUrl("/dashboard")         // where to go on success
                .failureUrl("/login?error"));

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(oidcLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
        );

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(gizmoOidcUserService))
                .defaultSuccessUrl("/dashboard", true)

        );

        // TODO valid if we always want redirection to Keycloak
//        http.exceptionHandling(ex -> ex
//                .authenticationEntryPoint(
//                        new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/keycloak")
//                )
//        );

        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .maximumSessions(1));
        //csrf and csp policies are set in GizmoApplication
        return http.build();
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri(postLogoutRedirectUri);
        return handler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new GizmoAuthProvider();
    }

}
