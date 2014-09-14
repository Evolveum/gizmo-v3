package sk.lazyman.gizmo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class GizmoUtils {

    public static String describeProject(Project project) {
        if (project == null) {
            return null;
        }

        return StringUtils.join(new Object[]{project.getName(), project.getCustomer().getName()}, " - ");
    }

    public static Date createTaskDefaultFrom() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.MONTH, -1);

        return cal.getTime();
    }

    public static Date createTaskDefaultTo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createTaskDefaultFrom());

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);

        return cal.getTime();
    }

    public static IModel<List<User>> createUserModel(final UserRepository repo) {
        return new LoadableModel<List<User>>(false) {

            @Override
            protected List<User> load() {
                return repo.findAll(new Sort(Sort.Direction.ASC, User.F_FIRST_NAME, User.F_LAST_NAME));
            }
        };
    }
}
