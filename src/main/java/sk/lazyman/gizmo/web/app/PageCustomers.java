package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;

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

    public PageCustomers() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextField searchText = new TextField(ID_SEARCH_TEXT, new Model());
        form.add(searchText);

        initButtons(form);

        BasicDataProvider provider = new BasicDataProvider(getCustomerRepository(), 25);
        provider.setSort(new Sort(Sort.Direction.ASC, Customer.F_NAME, Customer.F_TYPE));

        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<Customer>(createStringResource("Customer.name"), Customer.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Customer> rowModel) {
                customerDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Customer.type"), Customer.F_TYPE));
        columns.add(new PropertyColumn(createStringResource("Customer.description"), Customer.F_DESCRIPTION));
        columns.add(new PropertyColumn(createStringResource("Customer.partner"), Customer.F_PARTNER));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 25);
        add(table);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton search = new AjaxSubmitButton(ID_SEARCH,
                createStringResource("PageCustomers.search")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                searchPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        form.add(search);

        AjaxButton newCustomer = new AjaxButton(ID_NEW_CUSTOMER, createStringResource("PageCustomers.newCustomer")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newCustomerPerformed(target);
            }
        };
        form.add(newCustomer);
    }

    private void newCustomerPerformed(AjaxRequestTarget target) {
        setResponsePage(PageCustomer.class);
    }

    private void searchPerformed(AjaxRequestTarget target) {
        //todo implement
    }

    private void customerDetailsPerformed(AjaxRequestTarget target, Customer customer) {
        PageParameters params = new PageParameters();
        params.set(PageCustomer.CUSTOMER_ID, customer.getId());

        setResponsePage(PageCustomer.class, params);
    }
}
