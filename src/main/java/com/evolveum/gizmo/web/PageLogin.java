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

package com.evolveum.gizmo.web;

import com.evolveum.gizmo.component.MainFeedback;
import com.evolveum.gizmo.component.form.GizmoForm;
import com.evolveum.gizmo.security.GizmoApplication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/login")
public class PageLogin extends PageTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FEEDBACK = "feedback";

    public PageLogin() {
        if (getPrincipal() != null) {
            WebApplication app = GizmoApplication.get();
            setResponsePage(app.getHomePage());
        }

        initLayout();
    }

    private Object getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getPrincipal();
    }

    private void initLayout() {
        GizmoForm<Void> form = new GizmoForm<>(ID_FORM);
        add(form);

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        ServletWebRequest req = (ServletWebRequest) RequestCycle.get().getRequest();
        HttpServletRequest httpReq = req.getContainerRequest();
        HttpSession httpSession = httpReq.getSession();

        Exception ex = (Exception) httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex == null) {
            return;
        }

        String key = ex.getMessage() != null ? ex.getMessage() : "web.security.provider.unavailable";
        error(getString(key));

        httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
