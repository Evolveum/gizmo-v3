/*
 * Copyright 2020 Katarina Valalikova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.lazyman.gizmo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sk.lazyman.gizmo.security.GizmoAuthProvider;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig { //} extends WebSecurityConfigurerAdapter {


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {

            web.ignoring().requestMatchers(new AntPathRequestMatcher("/js/**"));
            web.ignoring().requestMatchers(new AntPathRequestMatcher("/css/**"));
            web.ignoring().requestMatchers(new AntPathRequestMatcher("/img/**"));
            web.ignoring().requestMatchers(new AntPathRequestMatcher("/fonts/**"));

            web.ignoring().requestMatchers(new AntPathRequestMatcher("/static/**"));

            web.ignoring().requestMatchers(new AntPathRequestMatcher("/wicket/resource/**"));

            web.ignoring().requestMatchers(new AntPathRequestMatcher("/favicon.ico"));
        };
    }
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(
//                "/static/js/**",
//                "/js/**",
//                "/static/css/**",
//                "/static/**",
//                "/css/**",
//                "/favicon.ico",
//                "/static/img/**",
//                "/static/fonts/**",
//                "/static/wicket/resource/**");
//    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(
                                antMatcher("/j_spring_security_check"),
                                antMatcher("/spring_security_login"),
                                antMatcher("/login"),
                                antMatcher("/error"),
                                antMatcher("/error/*"),
                                antMatcher("/bootstrap"),
                                antMatcher("/wicket/resource/**"))
                            .permitAll()
                        .anyRequest()
                            .authenticated());

        http.formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/spring_security_login"));


        http.logout((logout) -> logout
                .clearAuthentication(true)
                .logoutUrl("/j_spring_security_logout")
                .logoutSuccessUrl("/app/dashboard")
                .invalidateHttpSession(true));

        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .maximumSessions(1));

        http.csrf().disable();

        http.headers().disable();
//        http.headers(headers -> headers
//                        .disable()
//                        .defaultsDisabled()
//                        .xssProtection(HeadersConfigurer.XXssConfig::disable).defaultsDisabled()
//                .contentSecurityPolicy(contentSecurityPolicy -> contentSecurityPolicy
//                        .
//                        .policyDirectives(
//                                "default-src 'unsafe-inline'; " +
//                                "script-src 'strict-dynamic'; " +
//                                "style-src 'strict-dynamic';"))
//                        .policyDirectives("script-src 'self'; style-src 'self'; style-src-attr 'self'; script-src-elem 'self'; style-src-elem 'self';"))
//        );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new GizmoAuthProvider();
    }

}
