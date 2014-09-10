package sk.lazyman.gizmo.data.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class UsersDataProvider extends SortableDataProvider<User, String> {

    private UserRepository userRepository;

    public UsersDataProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Iterator<? extends User> iterator(long first, long count) {
        List<User> users = new ArrayList<>();

        List<User> found = userRepository.listUsers();
        if (found != null) {
            for (int i = 0; i < found.size(); i++) {
                if (i < first) {
                    continue;
                }

                if (i > first + count) {
                    break;
                }

                users.add(found.get(i));
            }
        }

        return users.iterator();
    }

    @Override
    public long size() {
        return userRepository.count();
    }

    @Override
    public IModel<User> model(User object) {
        return new Model<>(object);
    }
}
