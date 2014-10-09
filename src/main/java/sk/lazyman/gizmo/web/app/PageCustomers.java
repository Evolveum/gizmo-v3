package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
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

    public PageCustomers() {
        initLayout();
    }

    private void initLayout() {
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

    private void customerDetailsPerformed(AjaxRequestTarget target, Customer customer) {
        PageParameters params = new PageParameters();
        params.set(PageCustomer.CUSTOMER_ID, customer.getId());

        setResponsePage(PageCustomer.class, params);
    }
}
