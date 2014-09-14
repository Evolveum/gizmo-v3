package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import sk.lazyman.gizmo.data.EmailLog;

/**
 * @author lazyman
 */
public interface EmailLogRepository extends JpaRepository<EmailLog, Integer>, QueryDslPredicateExecutor<EmailLog> {

}
