package sk.lazyman.gizmo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.Company;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.ProjectPart;
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

    public static String describeProjectPart(ProjectPart part, String delimiter) {
        if (part == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (part.getProject() != null && part.getProject().getCustomer() != null) {
            Company c =  part.getProject().getCustomer();
            sb.append(c.getName());
        }

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        if (part.getProject() != null) {
            Project p =  part.getProject();
            sb.append(p.getName());
        }

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        sb.append(part.getName());

        return sb.toString();
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
