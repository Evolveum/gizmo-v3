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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import sk.lazyman.gizmo.security.GizmoAuthProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/gizmo/js/**");
        web.ignoring().antMatchers("/gizmo/css/**");
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/favicon.ico");
        web.ignoring().antMatchers("/gizmo/img/**");
        web.ignoring().antMatchers("/gizmo/fonts/**");

        web.ignoring().antMatchers("/static-web/**");
        web.ignoring().antMatchers("/gizmo/less/**");

        web.ignoring().antMatchers("/gizmo/wicket/resource/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/j_spring_security_check",
                        "/spring_security_login",
                        "/login",
                        "/error",
                        "/error/*",
                        "/bootstrap",
                        "/wicket/resource/**")
                    .permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/spring_security_login");

        http.logout()
                .clearAuthentication(true)
                .logoutUrl("/j_spring_security_logout")
                .logoutSuccessUrl("/app/dashboard")
                .invalidateHttpSession(true);

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .maximumSessions(1);

        http.csrf().disable();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new GizmoAuthProvider();
    }

}
