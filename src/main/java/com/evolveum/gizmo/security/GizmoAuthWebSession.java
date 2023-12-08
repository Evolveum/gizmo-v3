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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.evolveum.gizmo.dto.ReportFilterDto;

import java.util.Locale;

/**
 * @author lazyman
 */
public class GizmoAuthWebSession extends AuthenticatedWebSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoAuthWebSession.class);

    @SpringBean(name = "authenticationProvider")
    private AuthenticationProvider authenticationProvider;

    private ReportFilterDto dashboardFilter;
    private ReportFilterDto reportFilterDto;

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
        return new Roles();
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
            LOGGER.error("Couldn't authenticate user, reason: {}", ex.getMessage());
            LOGGER.debug("Couldn't authenticate user.", ex);
            authenticated = false;

            String msg = new StringResourceModel(ex.getMessage(),null, Model.of(ex.getMessage())).getString();
            error(msg);
        }

        return authenticated;
    }

    public ReportFilterDto getDashboardFilter() {
        return dashboardFilter;
    }

    public void setDashboardFilter(ReportFilterDto dashboardFilter) {
        this.dashboardFilter = dashboardFilter;
    }

    public ReportFilterDto getReportFilterDto() {
        return reportFilterDto;
    }

    public void setReportFilterDto(ReportFilterDto reportFilterDto) {
        this.reportFilterDto = reportFilterDto;
    }
}
