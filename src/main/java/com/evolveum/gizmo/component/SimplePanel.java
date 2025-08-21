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

package com.evolveum.gizmo.component;

import com.evolveum.gizmo.web.app.PageAppTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.LoggerFactory;

/**
 * @author lazyman
 */
public abstract class SimplePanel<T> extends Panel {

    private IModel<T> model;

    protected SimplePanel(String id) {
        this(id, null);
    }

    protected SimplePanel(String id, IModel<T> model) {
        super(id);
        this.model = model == null ? createModel() : model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    public IModel<T> createModel() {
        return null;
    }

    public void setModel(IModel<T> model) {
        this.model = model;
    }

    public IModel<T> getModel() {
        return model;
    }

    public T getModelObject() {
        return model != null ? model.getObject() : null;
    }

    public String getString(String resourceKey, Object... objects) {
        return createStringResource(resourceKey, objects).getString();
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, this)
                .setParameters(objects)
                .setDefaultValue(resourceKey);
    }

    public PageAppTemplate getPageTemplate() {
        WebPage page = getWebPage();
        if (page instanceof PageAppTemplate) {
            return (PageAppTemplate) page;
        }

        throw new IllegalStateException("Unexpcted page: " + page);
    }

    public StringResourceModel createStringResource(Enum e) {
        return createStringResource(e, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix) {
        return createStringResource(e, prefix, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix, String nullKey) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append('.');
        }

        if (e == null) {
            if (StringUtils.isNotEmpty(nullKey)) {
                sb.append(nullKey);
            } else {
                sb = new StringBuilder();
            }
        } else {
            sb.append(e.getDeclaringClass().getSimpleName()).append('.');
            sb.append(e.name());
        }

        return createStringResource(sb.toString());
    }

    protected String createComponentPath(String... components) {
        return StringUtils.join(components, ":");
    }

    protected abstract void initLayout();

    protected void handleGuiExceptionFromPanel(String message, Exception e, AjaxRequestTarget target) {
        if (target == null) {
            target = RequestCycle.get().find(AjaxRequestTarget.class).orElse(null);
        }
        Page page = getPage();
        if (page instanceof PageAppTemplate tem) {
            tem.handleGuiException(tem, message, e, target);
        } else {
            LoggerFactory.getLogger(getClass()).error("Error", e);
        }
    }

}
