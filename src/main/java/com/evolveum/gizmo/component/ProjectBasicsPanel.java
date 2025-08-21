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

import com.evolveum.gizmo.component.form.AreaFormGroup;
import com.evolveum.gizmo.component.form.CheckFormGroup;
import com.evolveum.gizmo.component.form.DropDownFormGroup;
import com.evolveum.gizmo.component.form.FormGroup;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.Project;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectBasicsPanel extends SimplePanel<Project> {

    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CUSTOMER = "customer";
    private static final String ID_CLOSED = "closed";
    private static final String ID_COMMERCIAL = "commercial";

    public ProjectBasicsPanel(String id, IModel<Project> model) {
        super(id, model);
    }

    protected void initLayout() {
        FormGroup name = new FormGroup(ID_NAME, new PropertyModel<String>(getModel(), Project.F_NAME),
                createStringResource("Project.name"), true);
        add(name);

        FormGroup description = new AreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(getModel(), Project.F_DESCRIPTION),
                createStringResource("Project.description"), false);
        add(description);

        DropDownFormGroup customer = new DropDownFormGroup(ID_CUSTOMER,
                new PropertyModel<Customer>(getModel(), Project.F_CUSTOMER),
                createStringResource("Project.customer"), true);
        add(customer);

        customer.setNullValid(false);
        customer.setChoices(new LoadableModel<List<Customer>>(false) {

            @Override
            protected List<Customer> load() {
                CustomerRepository repository = getPageTemplate().getCustomerRepository();
                List<Customer> list = repository.listCustomers();
                if (list == null) {
                    list = new ArrayList<>();
                }
                return list;
            }
        });
        customer.setRenderer(GizmoUtils.createCustomerChoiceRenderer());

        FormGroup closed = new CheckFormGroup(ID_CLOSED, new PropertyModel<Boolean>(getModel(), Project.F_CLOSED),
                createStringResource("Project.closed"), true);
        add(closed);

        FormGroup commercial = new CheckFormGroup(ID_COMMERCIAL, new PropertyModel<Boolean>(getModel(), Project.F_COMMERCIAL),
                createStringResource("Project.commercial"), true);
        add(commercial);


    }
}
