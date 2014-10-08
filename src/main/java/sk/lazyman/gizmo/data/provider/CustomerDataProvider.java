package sk.lazyman.gizmo.data.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.repository.CustomerRepository;

import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class CustomerDataProvider extends SortableDataProvider<Customer, String> {

    private CustomerRepository customerRepository;

    public CustomerDataProvider(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Iterator<Customer> iterator(long first, long count) {
        List<Customer> list = customerRepository.listCustomers();
        return list.iterator();
    }

    @Override
    public long size() {
        return customerRepository.count();
    }

    @Override
    public IModel<Customer> model(Customer object) {
        return new Model<>(object);
    }
}
