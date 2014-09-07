package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.lazyman.gizmo.data.Project;

/**
 * @author lazyman
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {

}
