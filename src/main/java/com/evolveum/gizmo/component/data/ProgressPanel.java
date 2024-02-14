/*
 *  Copyright (C) 2024 Evolveum
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

package com.evolveum.gizmo.component.data;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.dto.ProgressDto;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class ProgressPanel extends SimplePanel<ProgressDto> {

    private static final String ID_PROGRESS_BAR = "progressBar";

    public ProgressPanel(String id, IModel<ProgressDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        Label label = new Label(ID_PROGRESS_BAR, () -> "Progress: " + getModelObject().getCurrent() + " / " + getModelObject().getTotal());
        label.add(AttributeAppender.append("aria-valuenow", new PropertyModel<>(getModel(), "current")));
        label.add(AttributeAppender.append("aria-valuemax", new PropertyModel<>(getModel(), "total")));
        label.add(AttributeAppender.append("style", () -> "width: " + computeProgress() + "%"));
        add(label);
    }

    private double computeProgress() {
        return (getModelObject().getCurrent() / getModelObject().getTotal()) * 100;
    }
}
