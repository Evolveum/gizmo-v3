package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.ProjectPart;

import java.util.List;

/**
 * @author lazyman
 */
public interface ProjectPartRepository extends JpaRepository<ProjectPart, Integer> {

    @Query("from ProjectPart p where p.project.id = :projectId order by p.name")
    public List<ProjectPart> findProjectParts(@Param("projectId") Integer projectId);
}
