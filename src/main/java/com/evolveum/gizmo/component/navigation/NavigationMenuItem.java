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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class NavigationMenuItem implements Serializable {

    private IModel<String> name;
    private Class<? extends WebPage> page;

    public NavigationMenuItem(IModel<String> name, Class<? extends WebPage> page) {
        this.name = name;
        this.page = page;
    }

    public IModel<String> getName() {
        return name;
    }

    public void setName(IModel<String> name) {
        this.name = name;
    }

    public Class<? extends WebPage> getPage() {
        return page;
    }

    public void setPage(Class<? extends WebPage> page) {
        this.page = page;
    }
}
