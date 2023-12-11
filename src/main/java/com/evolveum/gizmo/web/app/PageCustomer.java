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

import com.evolveum.gizmo.component.*;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/customer")
public class PageCustomer extends PageAppCustomers {

    public static final String CUSTOMER_ID = "customerId";

    private static final String ID_FORM = "form";

    private static final String ID_TABS = "tabs";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";

    private final IModel<Customer> model;

    public PageCustomer() {
        this(null);
    }

    public PageCustomer(PageParameters params) {
        super(params);

        model = new LoadableModel<>() {

            @Override
            protected Customer load() {
                return loadCustomer();
            }
        };

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return () -> {
            Integer id = getCustomerId();
            String key = id != null ? "page.title.edit" : "page.title";
            return createStringResource(key).getString();
        };
    }

    private Customer loadCustomer() {
        Integer customerId = getCustomerId();
        if (customerId == null) {
            return new Customer();
        }

        CustomerRepository repository = getCustomerRepository();
        Optional<Customer> customer = repository.findById(customerId);
        if (customer == null || !customer.isPresent()) {
            getSession().error(translateString("Message.couldntFindCustomer", customerId));
            throw new RestartResponseException(PageCustomers.class);
        }

        return customer.get();
    }

    private Integer getCustomerId() {
        return getIntegerParam(CUSTOMER_ID);
    }

    private void initLayout() {
        Form<Customer> form = new Form<>(ID_FORM);
        add(form);

        initButtons(form);

        GizmoTabbedPanel<ITab> customerTabbedPanel = new GizmoTabbedPanel<>(ID_TABS, createTabs());
        form.add(customerTabbedPanel);

    }

    private List<ITab> createTabs() {
        List<ITab> tabList = new ArrayList<>();

        tabList.add(new AbstractTab(createStringResource("PageCustomer.basics")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new CustomerBasicsPanel(panelId, model);
            }
        });

        if (getCustomerId() != null) {
            tabList.add(new AbstractTab(createStringResource("PageCustomer.projects")) {

                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    return new ProjectsTab(panelId);
                }
            });
        }
        return tabList;
    }



    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                savePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(form);
            }
        };
        form.add(save);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageCustomers.class);
    }

    private void savePerformed(AjaxRequestTarget target) {
        CustomerRepository repository = getCustomerRepository();

        try {
            Customer customer = model.getObject();
            customer = repository.saveAndFlush(customer);

            PageParameters params = createPageParams(CUSTOMER_ID, customer.getId());
            PageCustomer page = new PageCustomer(params);
            page.success(getString("Message.customerSavedSuccessfully"));

            setResponsePage(page);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveCustomer", ex, target);
        }
    }
}
