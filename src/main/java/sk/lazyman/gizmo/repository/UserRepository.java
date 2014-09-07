package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.User;

/**
 * @author lazyman
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("from User u where u.userName = :username")
    public User findUserByUserName(@Param("username") String username);
}
