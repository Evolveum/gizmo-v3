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

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.data.provider.BasicDataProvider;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.data.LinkColumn;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.QCustomer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/customers")
public class PageCustomers extends PageAppCustomers {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_SEARCH_TEXT = "searchText";
    private static final String ID_SEARCH = "search";
    private static final String ID_NEW_CUSTOMER = "newCustomer";

    private IModel<String> searchModel = new Model<>();

    public PageCustomers() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextField searchText = new TextField(ID_SEARCH_TEXT, searchModel);
        searchText.setOutputMarkupId(true);
        form.add(searchText);

        initButtons(form);

        BasicDataProvider provider = new BasicDataProvider(getCustomerRepository()) {

            @Override
            public Predicate getPredicate() {
                String text = searchModel.getObject();
                if (StringUtils.isNotEmpty(text)) {
                    return QCustomer.customer.name.lower().contains(text.toLowerCase())
                            .or(QCustomer.customer.description.lower().contains(text.toLowerCase()));
                }

                return null;
            }
        };
        provider.setSort(Sort.by(Sort.Direction.ASC, Customer.F_NAME, Customer.F_TYPE));

        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<Customer>(createStringResource("Customer.name"), Customer.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Customer> rowModel) {
                customerDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Customer.type"), Customer.F_TYPE));
        columns.add(new PropertyColumn(createStringResource("Customer.description"), Customer.F_DESCRIPTION));
        columns.add(new PropertyColumn(createStringResource("Customer.partner"), Customer.F_PARTNER + "." + Customer.F_NAME));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);
    }

    private void initButtons(Form form) {
        AjaxButton search = new AjaxButton(ID_SEARCH, form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                searchPerformed(target);
            }
        };
        form.add(search);
        form.setDefaultButton(search);

    }

    @Override
    public Fragment createHeaderButtonsFragment(String fragmentId) {
        Fragment fragment = new  Fragment(fragmentId, "buttonsFragment", this);

        AjaxLink<String> newCustomer = new AjaxLink<>(ID_NEW_CUSTOMER, createStringResource("PageCustomers.newCustomer")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newCustomerPerformed();
            }
        };
        newCustomer.setOutputMarkupId(true);
        fragment.add(newCustomer);

        return fragment;
    }

    private void newCustomerPerformed() {
        setResponsePage(PageCustomer.class);
    }

    private void searchPerformed(AjaxRequestTarget target) {
        target.add(get(ID_TABLE));
    }

    private void customerDetailsPerformed(AjaxRequestTarget target, Customer customer) {
        PageParameters params = new PageParameters();
        params.set(PageCustomer.CUSTOMER_ID, customer.getId());

        setResponsePage(PageCustomer.class, params);
    }
}
