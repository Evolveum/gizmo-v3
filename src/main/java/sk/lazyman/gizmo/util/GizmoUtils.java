package sk.lazyman.gizmo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;

import java.util.*;

/**
 * @author lazyman
 */
public class GizmoUtils {

    public static final int DESCRIPTION_SIZE = 3000;

    public static String describeProject(Project project) {
        if (project == null) {
            return null;
        }

        return StringUtils.join(new Object[]{project.getName(), project.getCustomer().getName()}, " - ");
    }

    public static String describeProjectPart(Part part, String delimiter) {
        if (part == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (part.getProject() != null && part.getProject().getCustomer() != null) {
            Customer c = part.getProject().getCustomer();
            sb.append(c.getName());
        }

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        if (part.getProject() != null) {
            Project p = part.getProject();
            sb.append(p.getName());
        }

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        sb.append(part.getName());

        return sb.toString();
    }

    public static Date createWorkDefaultFrom() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.MONTH, -2);

        return cal.getTime();
    }

    public static Date createWorkDefaultTo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createWorkDefaultFrom());

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);

        return cal.getTime();
    }

    public static IModel<List<User>> createUserModel(final UserRepository repo) {
        return new LoadableModel<List<User>>(false) {

            @Override
            protected List<User> load() {
                return repo.findAll(new Sort(Sort.Direction.ASC, User.F_GIVEN_NAME, User.F_FAMILY_NAME));
            }
        };
    }

    public static <T extends Enum> IModel<String> createLocalizedModelForEnum(T value, Component comp) {
        String key = value != null ? value.getClass().getSimpleName() + "." + value.name() : "";
        return new StringResourceModel(key, comp, null);
    }

    public static <T extends Enum> IModel<List<T>> createReadonlyModelFromEnum(final Class<T> type) {
        return new AbstractReadOnlyModel<List<T>>() {

            @Override
            public List<T> getObject() {
                List<T> list = new ArrayList<T>();
                Collections.addAll(list, type.getEnumConstants());

                return list;
            }
        };
    }

    public static IChoiceRenderer<User> createUserChoiceRenderer() {
        return new IChoiceRenderer<User>() {

            @Override
            public Object getDisplayValue(User object) {
                return object != null ? object.getFullName() : null;
            }

            @Override
            public String getIdValue(User object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static IChoiceRenderer<Customer> createCustomerChoiceRenderer() {
        return new IChoiceRenderer<Customer>() {

            @Override
            public Object getDisplayValue(Customer object) {
                return object != null ? object.getName() : null;
            }

            @Override
            public String getIdValue(Customer object, int index) {
                return Integer.toString(index);
            }
        };
    }
}
