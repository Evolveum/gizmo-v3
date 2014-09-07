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

    @Query("from User u order by u.userName")
    List<User> listUsers();

    @Query("from User u where u.userName = :username")
    User findUserByUserName(@Param("username") String username);
}
