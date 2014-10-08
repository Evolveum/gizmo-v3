package sk.lazyman.gizmo.data.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.Iterator;

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
        Sort sort = new Sort(Sort.Direction.ASC, Customer.F_NAME, Customer.F_TYPE);
                        //todo fix paging everywhere, PageRequest(PAGE, size)
        PageRequest page = new PageRequest((int) first, (int) count, sort);
        Page<Customer> found = customerRepository.findAll(page);
        System.out.println(">> " + found);
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<Customer>().iterator();
    }

    @Override
    public long size() {
        System.out.println("count: " + customerRepository.count());
        return customerRepository.count();
    }

    @Override
    public IModel<Customer> model(Customer object) {
        return new Model<>(object);
    }
}
