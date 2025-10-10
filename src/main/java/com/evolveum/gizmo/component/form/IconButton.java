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

package com.evolveum.gizmo.component.form;

import com.evolveum.gizmo.component.SimplePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class IconButton extends SimplePanel<String> {

    private static final String ID_BUTTON = "button";
    private static final String ID_ICON = "icon";
    private static final String ID_LABEL = "label";

    private final IModel<String> buttonClass;
    private final IModel<String> iconModel;

    public IconButton(String id, IModel<String> label, IModel<String> iconModel, IModel<String> buttonClass) {
        super(id, label);
        this.buttonClass = buttonClass;
        this.iconModel = iconModel;
    }

    @Override
    protected void initLayout() {
        WebMarkupContainer wrapper = new WebMarkupContainer(ID_BUTTON);
        wrapper.add(AttributeAppender.append("class", "btn " + buttonClass.getObject()));
        wrapper.setOutputMarkupId(true);
        add(wrapper);

        WebMarkupContainer icon = new WebMarkupContainer(ID_ICON);
        icon.add(AttributeAppender.append("class", iconModel));
        wrapper.add(icon);

        Label label = new Label(ID_LABEL, getModel());
        label.setRenderBodyOnly(true);
        wrapper.add(label);
    }

    protected void submitPerformed(AjaxRequestTarget target) {
    }
}

