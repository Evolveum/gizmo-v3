package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.data.Log;

/**
 * @author lazyman
 */
public interface LogRepository extends JpaRepository<Log, Integer>,
        QueryDslPredicateExecutor<Log> {
}
