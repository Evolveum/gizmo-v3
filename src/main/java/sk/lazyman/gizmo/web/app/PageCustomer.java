package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.*;
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
        model = new LoadableModel<Customer>() {

            @Override
            protected Customer load() {
                return loadCustomer();
            }
        };

        initLayout();
    }

    private Customer loadCustomer() {
        PageParameters params = getPageParameters();
        StringValue val = params.get(CUSTOMER_ID);
        String customerId = val != null ? val.toString() : null;

        if (customerId == null || !customerId.matches("[0-9]+")) {
            return new Customer();
        }

        CustomerRepository repository = getCustomerRepository();
        Customer customer = repository.findOne(Integer.parseInt(customerId));
        if (customer == null) {
            getSession().error(translateString("Message.couldntFindCustomer", customerId));
            throw new RestartResponseException(PageCustomers.class);
        }

        return customer;
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
        form.add(type);

        DropDownFormGroup partner = new DropDownFormGroup(ID_PARTNER,
                new PropertyModel<Customer>(model, Customer.F_PARTNER),
                createStringResource("Customer.partner"), true);
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
        form.add(partner);

        List<ITab> tabList = new ArrayList<>();
        tabList.add(new AbstractTab(createStringResource("PageCustomer.projects")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ProjectsTab(panelId);
            }
        });
        tabList.add(new AbstractTab(createStringResource("PageCustomer.projectDetails")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ProjectDetailsTab(panelId);
            }
        });

        tabList.add(new AbstractTab(createStringResource("PageCustomer.logs")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new CustomerLogsTab(panelId);
            }
        });

        tabList.add(new AbstractTab(createStringResource("PageCustomer.notifications")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new CustomerNotificationsTab(panelId);
            }
        });

        AjaxBootstrapTabbedPanel tabs = new AjaxBootstrapTabbedPanel(ID_TABS, tabList);
        add(tabs);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
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
        setResponsePage(PageProjects.class);
    }

    private void savePerformed(AjaxRequestTarget target) {
        //todo implement
    }
}
