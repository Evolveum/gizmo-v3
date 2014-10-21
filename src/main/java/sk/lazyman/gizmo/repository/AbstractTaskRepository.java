package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import sk.lazyman.gizmo.data.AbstractTask;

/**
 * @author lazyman
 */
public interface AbstractTaskRepository extends JpaRepository<AbstractTask, Integer>,
        QueryDslPredicateExecutor<AbstractTask> {
}
