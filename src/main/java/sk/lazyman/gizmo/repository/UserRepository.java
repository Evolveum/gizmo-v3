package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.lazyman.gizmo.data.User;

/**
 * @author lazyman
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("from User u where u.userName = ?")
    public User findUserByUserName(String userName);
}
