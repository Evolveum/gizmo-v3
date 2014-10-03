package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.User;

import java.util.List;

/**
 * @author lazyman
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("from User u order by u.name")
    List<User> listUsers();

    @Query("from User u where u.name = :name")
    User findUserByName(@Param("name") String name);
}
