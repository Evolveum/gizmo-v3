package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.lazyman.gizmo.data.ProjectPart;

/**
 * @author lazyman
 */
public interface ProjectPartRepository extends JpaRepository<ProjectPart, Integer> {

}
