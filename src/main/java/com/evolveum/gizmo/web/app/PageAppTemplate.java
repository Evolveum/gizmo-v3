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

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.MainFeedback;
import com.evolveum.gizmo.component.navigation.NavigationMenuItem;
import com.evolveum.gizmo.component.navigation.NavigationPanel;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.evolveum.gizmo.util.LoadableModel;
import com.evolveum.gizmo.web.PageTemplate;
import com.evolveum.gizmo.web.error.PageError;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class PageAppTemplate extends PageTemplate {

    private static final String ID_NAVBAR = "navbar";
    private static final String ID_TITLE = "titleHeader";
    private static final String ID_FEEDBACK = "feedback";

    protected static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    protected static final String TEXT_SIZE = "col-sm-5 col-md-4";
    protected static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

    public PageAppTemplate() {
        this(null);
    }

    public PageAppTemplate(PageParameters parameters) {
        super(parameters);

        initLayout();
    }

    private void initLayout() {

        LoadableModel<List<NavigationMenuItem>> menuModel = new LoadableModel<List<NavigationMenuItem>>() {

            @Override
            protected List<NavigationMenuItem> load() {
                List<NavigationMenuItem> menuItems = new ArrayList<>();
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.dashboard"), PageDashboard.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.workReport"), PageWorkReport.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.customers"), PageCustomers.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.projects"), PageProjects.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.users"), PageUsers.class));
//                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.emails"), PageEmails.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.reports"), PageReports.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.calendar"), PageCalendar.class));
                menuItems.add(new NavigationMenuItem(createStringResource("PageAppTemplate.menu.labels"), PageLabels.class));
                return menuItems;
            }
        };
        NavigationPanel navbar = new NavigationPanel(ID_NAVBAR, menuModel);
        navbar.setOutputMarkupId(true);
        add(navbar);
        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        Fragment fragment = createHeaderButtonsFragment("buttons");
        add(fragment);

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    public Fragment createHeaderButtonsFragment(String fragmentId) {
        return new  Fragment(fragmentId, "buttonsFragment", this);
    }

    public MainFeedback getFeedbackPanel() {
        return (MainFeedback) get(ID_FEEDBACK);
    }

    private PageParameters createUserPageParams() {
        PageParameters params = new PageParameters();

        GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
        params.set(PageUser.USER_ID, principal.getUserId());

        return params;
    }

    public Integer getIntegerParam(String paramName) {
        PageParameters params = getPageParameters();
        StringValue val = params.get(paramName);
        String id = val != null ? val.toString() : null;

        if (id == null || !id.matches("[0-9]+")) {
            return null;
        }

        return Integer.parseInt(id);
    }

    public PageParameters createPageParams(String paramName, Integer value) {
        PageParameters params = new PageParameters();
        params.add(paramName, value);
        return params;
    }

    private IModel<String> createUsernameModel() {
        return new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                return principal.getFullName();
            }
        };
    }

    public void handleGuiException(PageAppTemplate page, Exception ex, AjaxRequestTarget target) {
        handleGuiException(page, getString("Message.unknownError"), ex, target);
    }

    public void handleGuiException(PageAppTemplate page, String message, Exception ex, AjaxRequestTarget target) {
        Logger LOG = LoggerFactory.getLogger(page.getClass());
        LOG.error("Exception occurred, {}, reason: {}", message, ex.getMessage());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exception occurred, {}", ex);
        }

        if (target != null) {
            page.error(createStringResource(message, ex.getMessage()).getString());

            target.add(page.getFeedbackPanel());
        } else {
            PageError errorPage = new PageError();
            errorPage.error(createStringResource(message, ex.getMessage()).getString());

            throw new RestartResponseException(errorPage);
        }
    }
}
