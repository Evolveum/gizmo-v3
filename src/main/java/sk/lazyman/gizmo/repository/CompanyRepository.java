package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.lazyman.gizmo.data.Company;

import java.util.List;

/**
 * @author lazyman
 */
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    @Query("from Company")
    public List<Company> listCompanies();
}
