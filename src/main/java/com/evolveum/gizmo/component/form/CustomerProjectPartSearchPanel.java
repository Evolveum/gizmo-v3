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

package com.evolveum.gizmo.component.form;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class CustomerProjectPartSearchPanel extends SimplePanel<ProjectSearchSettings> {

    private static final String ID_CUSTOMER_SEARCH = "customerSearch";
    private static final String ID_PROJECT_SEARCH = "projectSearch";
    private static final String ID_PART_SEARCH = "partSearch";
    private static final String ID_CUSTOMER = "customer";

    private LoadableModel<List<CustomerProjectPartDto>> availabelProjects;
    private boolean isForSearch = true;

    public CustomerProjectPartSearchPanel(String id, IModel<ProjectSearchSettings> model) {
        super(id, model);
    }

    protected void initLayout() {

        availabelProjects = GizmoUtils.createCustomerProjectPartList(getPageTemplate(), getModel());
        AjaxCheckBox customerSearch = new AjaxCheckBox(ID_CUSTOMER_SEARCH, new PropertyModel<>(getModel(), ProjectSearchSettings.F_CUSTOMER_SEARCH)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        customerSearch.setEnabled(false);
        customerSearch.setVisible(isForSearch);
        customerSearch.setOutputMarkupId(true);
        add(customerSearch);

        AjaxCheckBox projectSearch = new AjaxCheckBox(ID_PROJECT_SEARCH, new PropertyModel<>(getModel(), ProjectSearchSettings.F_PROJECT_SEARCH)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!CustomerProjectPartSearchPanel.this.getModelObject().isProjectSearch()) {
                    CustomerProjectPartSearchPanel.this.getModelObject().setPartSearch(false);
                }
                refreshSearch(target);
            }
        };
        projectSearch.setVisible(isForSearch);
        projectSearch.setOutputMarkupId(true);
        add(projectSearch);


        AjaxCheckBox partSearch = new AjaxCheckBox(ID_PART_SEARCH, new PropertyModel<>(getModel(), ProjectSearchSettings.F_PART_SEARCH)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                refreshSearch(target);
            }
        };
        partSearch.add(new VisibleEnableBehaviour(){
            @Override
            public boolean isEnabled() {
                return getModelObject().isProjectSearch();
            }

            @Override
            public boolean isVisible() {
                return isForSearch;
            }
        });
        partSearch.setOutputMarkupId(true);
        add(partSearch);

        MultiselectDropDownInput<CustomerProjectPartDto> customerCombo = new MultiselectDropDownInput<>(ID_CUSTOMER,
                new PropertyModel<>(getModel(), ProjectSearchSettings.F_CUSTOMER),
                availabelProjects,
                GizmoUtils.createCustomerProjectPartRenderer());
        customerCombo.setOutputMarkupId(true);
        customerCombo.add(new EmptyOnChangeAjaxBehavior());
        add(customerCombo);

        WebMarkupContainer customerContainer = new WebMarkupContainer("label");
        customerContainer.setVisible(!isForSearch);
        add(customerContainer);

    }

    private void refreshSearch(AjaxRequestTarget target) {
        availabelProjects.reset();
        target.add(CustomerProjectPartSearchPanel.this);
    }

    public void update(AjaxRequestTarget target) {
        refreshSearch(target);
    }
}
