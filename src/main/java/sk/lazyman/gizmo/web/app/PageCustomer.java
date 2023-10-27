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

package sk.lazyman.gizmo.web.app;

import org.apache.commons.lang3.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.ProjectsTab;
import sk.lazyman.gizmo.component.VisibleEnableBehaviour;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.DropDownFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.CustomerType;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

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
    private static final String ID_NAME = "name";
    private static final String ID_TYPE = "type";
    private static final String ID_PARTNER = "partner";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TABS = "tabs";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";

    private IModel<Customer> model;

    public PageCustomer() {
        this(null);
    }

    public PageCustomer(PageParameters params) {
        super(params);

        model = new LoadableModel<Customer>() {

            @Override
            protected Customer load() {
                return loadCustomer();
            }
        };

        initLayout();
    }

    public PageCustomer(PageParameters params, IModel<Customer> model) {
        super(params);

        Validate.notNull(model, "Model must not be null");
        this.model = model;

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return new IModel<String>() {

            @Override
            public String getObject() {
                Integer id = getIntegerParam(CUSTOMER_ID);
                String key = id != null ? "page.title.edit" : "page.title";
                return createStringResource(key).getString();
            }
        };
    }

    private Customer loadCustomer() {
        Integer customerId = getIntegerParam(CUSTOMER_ID);
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

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        initButtons(form);

        FormGroup name = new FormGroup(ID_NAME, new PropertyModel<String>(model, Customer.F_NAME),
                createStringResource("Customer.name"), true);
        form.add(name);

        AreaFormGroup description = new AreaFormGroup(ID_DESCRIPTION,
                new PropertyModel<String>(model, Customer.F_DESCRIPTION),
                createStringResource("Customer.description"), false);
        description.setRows(3);
        form.add(description);

        DropDownFormGroup type = new DropDownFormGroup(ID_TYPE,
                new PropertyModel<CustomerType>(model, Customer.F_TYPE),
                createStringResource("Customer.type"), true);
        type.setChoices(GizmoUtils.createReadonlyModelFromEnum(CustomerType.class));
        type.setRenderer(new EnumChoiceRenderer(this));
        DropDownChoice choice = (DropDownChoice) type.getFormComponent();
        form.add(type);

        final DropDownFormGroup partner = new DropDownFormGroup(ID_PARTNER,
                new PropertyModel<Customer>(model, Customer.F_PARTNER),
                createStringResource("Customer.partner"), false);
        partner.setOutputMarkupId(true);
        partner.setChoices(new LoadableModel<List<Customer>>(false) {

            @Override
            protected List<Customer> load() {
                CustomerRepository repository = getCustomerRepository();
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
                Customer customer = model.getObject();
                return !CustomerType.PARTNER.equals(customer.getType());
            }
        });
        form.add(partner);

        choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Customer customer = model.getObject();
                if (CustomerType.PARTNER.equals(customer.getType())) {
                    customer.setPartner(null);
                }
                target.add(partner);
            }
        });

        List<ITab> tabList = new ArrayList<>();
        tabList.add(new AbstractTab(createStringResource("PageCustomer.projects")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ProjectsTab(panelId);
            }
        });

//        tabList.add(new AbstractTab(createStringResource("PageCustomer.logs")) {
//
//            @Override
//            public WebMarkupContainer getPanel(String panelId) {
//                return new CustomerLogsTab(panelId);
//            }
//        });
//
//        tabList.add(new AbstractTab(createStringResource("PageCustomer.notifications")) {
//
//            @Override
//            public WebMarkupContainer getPanel(String panelId) {
//                return new CustomerNotificationsTab(panelId);
//            }
//        });

        AjaxTabbedPanel tabs = new AjaxTabbedPanel(ID_TABS, tabList);
//        AjaxBootstrapTabbedPanel tabs = new AjaxBootstrapTabbedPanel(ID_TABS, tabList);
        add(tabs);
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
