package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.Project;

import java.util.List;

/**
 * @author lazyman
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Query("from Project p where p.customer.id = :customerId order by p.name")
    public List<Project> findProjects(@Param("customerId") Integer companyId);
}
