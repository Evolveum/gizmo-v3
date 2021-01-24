package sk.lazyman.gizmo.web.app;

import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/app/vacations")
public class PageVacations extends PageAppTemplate {

    private static final String ID_CALENDAR = "calendar";
    public PageVacations() {
        initLayout();
    }

    private void initLayout() {

    }
}
