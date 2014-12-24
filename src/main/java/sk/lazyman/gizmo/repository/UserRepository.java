package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.User;

import java.util.List;

/**
 * @author lazyman
 */
public interface UserRepository extends JpaRepository<User, Integer>, QueryDslPredicateExecutor<User> {

    @Query("from User u order by u.name")
    List<User> listUsers();

    @Query("from User u where u.name = :name")
    User findUserByName(@Param("name") String name);

    @Query("from User u where u.enabled = true order by u.givenName asc, u.familyName asc")
    List<User> findAllEnabledUsers();
}
