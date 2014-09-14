package sk.lazyman.gizmo.web.app;


import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath(value = "/app/dashboard", alt = "/app")
public class PageDashboard extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_PROJECT = "project";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_BTN_DISPLAY = "display";
    private static final String ID_BTN_EMAIL = "email";
    private static final String ID_BTN_PRINT = "print";
    private static final String ID_BTN_NEW_TASK = "task";

    public PageDashboard() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        form.add(new DateTextField(ID_FROM));
        form.add(new DateTextField(ID_TO));
    }
}
