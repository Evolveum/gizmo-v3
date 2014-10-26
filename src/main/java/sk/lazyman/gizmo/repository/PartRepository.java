package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.Part;

import java.util.List;

/**
 * @author lazyman
 */
public interface PartRepository extends JpaRepository<Part, Integer>, QueryDslPredicateExecutor<Part> {

    @Query("from Part p where p.project.id = :projectId order by p.name")
    public List<Part> findParts(@Param("projectId") Integer projectId);

    @Query("from Part p where p.project.closed = false")
    List<Part> findOpenedProjectParts();
}
