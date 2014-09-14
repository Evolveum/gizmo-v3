package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.Task;

import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public interface TaskRepository extends JpaRepository<Task, Integer>, QueryDslPredicateExecutor<Task> {

    @Query("from Task t where t.date > :from and t.date <= :to")
    public List<Task> findTasks(@Param("from") Date from, @Param("to") Date to);

}
