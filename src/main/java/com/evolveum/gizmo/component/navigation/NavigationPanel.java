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

import com.evolveum.gizmo.component.LabeledLink;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.web.app.PageUser;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.web.app.PageDashboard;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

public class NavigationPanel extends SimplePanel<List<NavigationMenuItem>> {

    private static final String ID_HOME = "home";
    private static final String ID_MENU_ITEMS = "menuItems";
    private static final String ID_MENU_ITEM = "menuItem";
    private static final String ID_USER = "user";

    public NavigationPanel(String id, IModel<List<NavigationMenuItem>> model) {
        super(id, model);
    }

    @Override
    public void initLayout() {

        AjaxLink<Void> home = new AjaxLink<>(ID_HOME) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(PageDashboard.class);
            }
        };
        add(home);

        ListView<NavigationMenuItem> listView = new ListView<NavigationMenuItem>(ID_MENU_ITEMS, getModel()) {

            @Override
            protected void populateItem(ListItem<NavigationMenuItem> item) {
                item.add(new NavigationLink(ID_MENU_ITEM, item.getModel()));
            }
        };
        add(listView);

        String fullName = SecurityUtils.getPrincipalUser().getFullName();
        LabeledLink user = new LabeledLink(ID_USER, () -> fullName) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Integer userId = SecurityUtils.getPrincipalUser().getUserId();
                PageParameters params = new PageParameters();
                params.add(PageUser.USER_ID, userId);
                setResponsePage(PageUser.class, params);
            }
        };
        add(user);


    }


}
