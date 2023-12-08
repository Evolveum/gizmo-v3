/*
 * Copyright 2015 Viliam Repan (lazyman)
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

package com.evolveum.gizmo.web;

import com.evolveum.gizmo.component.MainFeedback;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.web.app.PageDashboard;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.security.GizmoApplication;

/**
 * @author lazyman
 */
@MountPath("/login")
public class PageLogin extends PageTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_USERNAME = "username";
    private static final String ID_USERNAME_GROUP = "usernameGroup";
    private static final String ID_PASSWORD = "password";
    private static final String ID_PASSWORD_GROUP = "passwordGroup";
    private static final String ID_BTN_SIGNIN = "signin";
    private static final String ID_FEEDBACK = "feedback";

    private IModel<LoginDto> model = new Model(new LoginDto());

    public PageLogin() {
        if (getPrincipal() != null) {
            WebApplication app = GizmoApplication.get();
            setResponsePage(app.getHomePage());
        }

        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        response.render(CssHeaderItem.forReference(new LessResourceReference(PageLogin.class, "PageLogin.less")));
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

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);



//        Form<?> form = new Form<>(ID_FORM) {
//
//            @Override
//            protected void onSubmit() {
//                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
//
//                LoginDto dto = model.getObject();
//                if (session.authenticate(dto.getUsername(), dto.getPassword())) {
//                    setResponsePage(PageDashboard.class);
//                }
//            }
//        };
//        add(form);

//        RequiredTextField username = new RequiredTextField(ID_USERNAME, new PropertyModel(model, LoginDto.F_USERNAME));
//        FormUtils.addPlaceholderAndLabel(username, createStringResource("PageLogin.username"));
//        form.add(username);//FormUtils.createFormGroup(ID_USERNAME_GROUP, username));
//
//        PasswordTextField password = new PasswordTextField(ID_PASSWORD, new PropertyModel(model, LoginDto.F_PASSWORD));
//        FormUtils.addPlaceholderAndLabel(password, createStringResource("PageLogin.password"));
//        form.add(password);//FormUtils.createFormGroup(ID_PASSWORD_GROUP, password));
//


//        AjaxSubmitLink signin = new AjaxSubmitLink(ID_BTN_SIGNIN) {
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                target.add(PageLogin.this.get(ID_FEEDBACK));
//            }
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                loginPerformed(target);
//            }
//        };
//        form.add(signin);
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

    private void loginPerformed(AjaxRequestTarget target) {
        target.add(PageLogin.this.get(ID_FEEDBACK));

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();

        LoginDto dto = model.getObject();
        if (session.authenticate(dto.getUsername(), dto.getPassword())) {
            setResponsePage(PageDashboard.class);
        }
    }
}
