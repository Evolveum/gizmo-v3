package sk.lazyman.gizmo.util;

import org.apache.commons.lang.StringUtils;
import sk.lazyman.gizmo.data.Project;

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
}
