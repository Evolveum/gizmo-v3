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

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.util.List;

public class GizmoTabbedPanel<T extends ITab> extends AjaxTabbedPanel<T> {


    public GizmoTabbedPanel(String id, List<T> tabs) {
        super(id, tabs);
    }

    @Override
    protected String getSelectedTabCssClass() {
        return "";
    }

    @Override
    protected WebMarkupContainer newLink(String linkId, int index) {
        WebMarkupContainer tabLink = super.newLink(linkId, index);
        tabLink.add(AttributeAppender.append("class", () -> getSelectedTab() == index ? " active" : ""));
        return tabLink;
    }
}
