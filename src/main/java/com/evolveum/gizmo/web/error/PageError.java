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

package com.evolveum.gizmo.web.error;

import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.MainFeedback;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.web.PageTemplate;
import com.evolveum.gizmo.web.app.PageDashboard;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.WebResponse;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Date;

/**
 * Base class for error web pages.
 *
 * @author lazyman
 */
@MountPath("/error")
public class PageError extends PageTemplate {

    private static final String ID_MESSAGE = "message";
    private static final String ID_BACK = "back";
    private static final String ID_TITLE = "titleHeader";
    private static final String ID_FEEDBACK = "feedback";

    private Integer code;
    private String exClass;
    private String exMessage;

    public PageError() {
        this(500);
    }

    public PageError(Integer code) {
        this(code, null);
    }

    public PageError(Exception ex) {
        this(500, ex);
    }

    public PageError(Integer code, Exception ex) {
        this.code = code;

        if (ex != null) {
            exClass = ex.getClass().getName();
            exMessage = ex.getMessage();
        }

        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        final IModel<String> message = new IModel<String>() {

            @Override
            public String getObject() {
                if (exClass == null) {
                    return null;
                }

                return GizmoUtils.formatDate(new Date()) + "\t" + exClass + ": " + exMessage;
            }
        };

        Label label = new Label(ID_MESSAGE, message);
        label.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return StringUtils.isNotEmpty(message.getObject());
            }
        });
        add(label);

        AjaxButton back = new AjaxButton(ID_BACK, createStringResource("PageError.button.back")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(PageDashboard.class);
            }
        };
        add(back);

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    private int getCode() {
        return code != null ? code : 500;
    }

    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);

        response.setStatus(getCode());
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    protected MainFeedback getFeedbackPanel() {
        return (MainFeedback) get(ID_FEEDBACK);
    }
}
