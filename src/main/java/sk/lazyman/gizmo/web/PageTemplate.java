package sk.lazyman.gizmo.web;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Alert;
import org.apache.wicket.Component;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import sk.lazyman.gizmo.repository.*;

/**
 * @author lazyman
 */
public class PageTemplate extends WebPage {

    private static final String ID_DEBUG_PANEL = "debugPanel";
    private static final String ID_TITLE = "title";

    @SpringBean
    private CompanyRepository companyRepository;
    @SpringBean
    private UserRepository userRepository;
    @SpringBean
    private ProjectRepository projectRepository;
    @SpringBean
    private ProjectPartRepository projectPartRepository;
    @SpringBean
    private TaskRepository taskRepository;
    @SpringBean
    private EmailLogRepository emailLogRepository;

    public PageTemplate() {
        Injector.get().inject(this);

        initLayout();
    }

    private void initLayout() {
        Label title = new Label(ID_TITLE, createPageTitleModel());
        title.setRenderBodyOnly(true);
        add(title);

        DebugBar debugPanel = new DebugBar(ID_DEBUG_PANEL);
        add(debugPanel);
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, this, new Model<String>(), resourceKey, objects);
    }

    public StringResourceModel createStringResource(Enum e) {
        String resourceKey = e.getDeclaringClass().getSimpleName() + "." + e.name();
        return createStringResource(resourceKey);
    }

    public static StringResourceModel createStringResourceStatic(Component component, String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, component, new Model<String>(), resourceKey, objects);
    }

    public static StringResourceModel createStringResourceStatic(Component component, Enum e) {
        String resourceKey = e.getDeclaringClass().getSimpleName() + "." + e.name();
        return createStringResourceStatic(component, resourceKey);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        Bootstrap.renderHead(response);
    }

    protected IModel<String> createPageTitleModel() {
        return createStringResource("page.title");
    }

    public void addAlert(IModel<String> text, Alert.Type type) {

    }

    public CompanyRepository getCompanyRepository() {
        return companyRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public ProjectPartRepository getProjectPartRepository() {
        return projectPartRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public EmailLogRepository getEmailLogRepository() {
        return emailLogRepository;
    }
}
