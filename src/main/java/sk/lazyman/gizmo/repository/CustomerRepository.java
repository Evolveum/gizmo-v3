package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import sk.lazyman.gizmo.data.Customer;

import java.util.List;

/**
 * @author lazyman
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer>, QueryDslPredicateExecutor<Customer> {

    @Query("from Customer c order by c.name")
    public List<Customer> listCustomers();

    @Query("from Customer c where c.type = 2 order by c.name")
    public List<Customer> listPartners();
}
