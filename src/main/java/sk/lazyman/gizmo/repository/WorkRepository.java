package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.Work;

import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public interface WorkRepository extends JpaRepository<Work, Integer>, QueryDslPredicateExecutor<Work> {

    @Query("from Work w where w.date > :from and w.date <= :to")
    public List<Work> findTasks(@Param("from") Date from, @Param("to") Date to);

}
