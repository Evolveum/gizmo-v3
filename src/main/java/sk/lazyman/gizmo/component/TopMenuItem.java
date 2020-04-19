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

package sk.lazyman.gizmo.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Activatable;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarAjaxLink;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author lazyman
 */
public class TopMenuItem<T> extends NavbarAjaxLink<T> implements Activatable {

    private Class<? extends WebPage> page;
    private PageParameters params;
    private Boolean active;

    public TopMenuItem(IModel label, Class<? extends WebPage> page) {
        this(label, page, null);
    }

    public TopMenuItem(IModel label, Class<? extends WebPage> page, PageParameters params) {
        super(label);

        Validate.notNull(page, "Page must not be null.");
        this.page = page;
        this.params = params;
    }

    @Override
    public boolean isActive(Component item) {
        if (active != null) {
            return active;
        }

        Page p = getPage();

        if (p != null && p.getClass().equals(page)) {
            return true;
        }

        return false;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        if (params != null) {
            setResponsePage(page, params);
            return;
        }

        setResponsePage(page);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
