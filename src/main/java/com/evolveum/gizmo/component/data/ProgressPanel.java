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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class ProgressPanel extends SimplePanel<ProgressDto> {

    private static final String ID_PROGRESS_BAR = "progressBar";
    private static final String ID_PROGRESS= "progress";
    private static final String ID_PROGRESS_TEXT = "progressText";

    public ProgressPanel(String id, IModel<ProgressDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        WebMarkupContainer wrapper = new WebMarkupContainer(ID_PROGRESS);
        wrapper.setOutputMarkupId(true);
        wrapper.add(AttributeAppender.append("class", " progress"));
        wrapper.add(AttributeAppender.append("style",
                "position:relative;" +
                        "height:22px;" +
                        "border:1px solid #aaa;" +
                        "border-radius:8px;" +
                        "background:white;" +
                        "overflow:hidden;"));
        add(wrapper);

        Label bar = new Label(ID_PROGRESS_BAR);
        bar.setOutputMarkupId(true);

        bar.add(AttributeAppender.append("aria-valuenow", new PropertyModel<>(getModel(), "current")));
        bar.add(AttributeAppender.append("aria-valuemax", new PropertyModel<>(getModel(), "total")));

        bar.add(AttributeAppender.append("style", () ->
                "width:" + computeProgress() + "%;" +
                        "height:100%;" +
                        "border-radius:8px 0 0 8px;" +
                        "transition:width .3s ease;" +
                        "background-color:#0d6efd;"));

        wrapper.add(bar);

        Label text = new Label(ID_PROGRESS_TEXT, () ->
                "Progress: " + getModelObject().getCurrent() + " / " + getModelObject().getTotal());
        text.setOutputMarkupId(true);
        text.add(AttributeAppender.append("style", () -> {
            double p = computeProgress();
            String color = (p >= 10) ? "white" : "black";
            return "position:absolute;" +
                    "inset:0;" +
                    "display:flex; align-items:center;" +
                    "padding-left:10px;" +
                    "font-size:12px;" +
                    "font-weight:500;" +
                    "pointer-events:none;" +
                    "color:" + color + ";";
        }));
        wrapper.add(text);
    }

    private double computeProgress() {
        return (getModelObject().getCurrent() / getModelObject().getTotal()) * 100;
    }
}
