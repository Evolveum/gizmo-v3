package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.lazyman.gizmo.data.Customer;

import java.util.List;

/**
 * @author lazyman
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("from Customer c order by c.name")
    public List<Customer> listCustomers();
}
