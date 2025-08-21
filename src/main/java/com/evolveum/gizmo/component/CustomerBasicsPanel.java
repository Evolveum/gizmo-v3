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
import com.evolveum.gizmo.component.form.DropDownFormGroup;
import com.evolveum.gizmo.component.form.FormGroup;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.CustomerType;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerBasicsPanel extends SimplePanel<Customer> {

    private static final String ID_NAME = "name";
    private static final String ID_TYPE = "type";
    private static final String ID_PARTNER = "partner";
    private static final String ID_DESCRIPTION = "description";

    public CustomerBasicsPanel(String id, IModel<Customer> model) {
        super(id, model);
    }

    protected void initLayout() {
        FormGroup name = new FormGroup(ID_NAME, new PropertyModel<String>(getModel(), Customer.F_NAME),
                createStringResource("Customer.name"), true);
        add(name);

        AreaFormGroup description = new AreaFormGroup(ID_DESCRIPTION,
                new PropertyModel<String>(getModel(), Customer.F_DESCRIPTION),
                createStringResource("Customer.description"), false);
        add(description);
        description.setRows(3);

        DropDownFormGroup type = new DropDownFormGroup(ID_TYPE,
                new PropertyModel<CustomerType>(getModel(), Customer.F_TYPE),
                createStringResource("Customer.type"), true);
        add(type);
        type.setChoices(GizmoUtils.createReadonlyModelFromEnum(CustomerType.class));
        type.setRenderer(new EnumChoiceRenderer(this));
        DropDownChoice choice = (DropDownChoice) type.getFormComponent();

        final DropDownFormGroup partner = new DropDownFormGroup(ID_PARTNER,
                new PropertyModel<Customer>(getModel(), Customer.F_PARTNER),
                createStringResource("Customer.partner"), false);
        add(partner);
        partner.setOutputMarkupId(true);
        partner.setChoices(new LoadableModel<List<Customer>>(false) {

            @Override
            protected List<Customer> load() {
                CustomerRepository repository = getPageTemplate().getCustomerRepository();
                List<Customer> list = repository.listPartners();
                if (list == null) {
                    list = new ArrayList<>();
                }
                return list;
            }
        });
        partner.setRenderer(GizmoUtils.createCustomerChoiceRenderer());
        partner.setNullValid(true);
        partner.setDefaultChoice(null);
        partner.getFormInput().add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                Customer customer = getModel().getObject();
                return !CustomerType.PARTNER.equals(customer.getType());
            }
        });

        choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Customer customer = getModel().getObject();
                if (CustomerType.PARTNER.equals(customer.getType())) {
                    customer.setPartner(null);
                }
                target.add(partner);
            }
        });

    }
}
