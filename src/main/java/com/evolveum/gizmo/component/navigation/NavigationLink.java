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

package com.evolveum.gizmo.component.navigation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

public class NavigationLink extends AjaxLink<NavigationMenuItem> {

    private WebPage webPage;
    private boolean active;

    public NavigationLink(String id, IModel<NavigationMenuItem> model) {
        super(id, model);

    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        setResponsePage(getModelObject().getPage());
    }

    @Override
    public IModel<?> getBody() {
        return getModelObject().getName();
    }
}
