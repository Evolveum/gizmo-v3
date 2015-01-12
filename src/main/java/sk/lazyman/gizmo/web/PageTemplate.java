package sk.lazyman.gizmo.web;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.commons.lang.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.env.Environment;
import sk.lazyman.gizmo.repository.*;

import javax.persistence.EntityManager;

/**
 * @author lazyman
 */
public class PageTemplate extends WebPage {

    private static final String ID_DEBUG_PANEL = "debugPanel";
    private static final String ID_TITLE = "title";

    @SpringBean
    private EntityManager entityManager;
    @SpringBean
    private CustomerRepository customerRepository;
    @SpringBean
    private UserRepository userRepository;
    @SpringBean
    private ProjectRepository projectRepository;
    @SpringBean
    private PartRepository projectPartRepository;
    @SpringBean
    private WorkRepository workRepository;
    @SpringBean
    private EmailLogRepository emailLogRepository;
    @SpringBean
    private LogRepository logRepository;
    @SpringBean
    private AbstractTaskRepository abstractTaskRepository;
    @SpringBean
    private Environment environment;

    public PageTemplate() {
        this(null);
    }

    public PageTemplate(PageParameters parameters) {
        super(parameters);

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

    public String translateString(String resourceKey, Object... objects) {
        return createStringResource(resourceKey, objects).getString();
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
        response.render(CssHeaderItem.forReference(
                new LessResourceReference(PageTemplate.class, "PageTemplate.less")));
    }

    protected IModel<String> createPageTitleModel() {
        return createStringResource("page.title");
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public PartRepository getProjectPartRepository() {
        return projectPartRepository;
    }

    public WorkRepository getWorkRepository() {
        return workRepository;
    }

    public EmailLogRepository getEmailLogRepository() {
        return emailLogRepository;
    }

    public LogRepository getLogRepository() {
        return logRepository;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public AbstractTaskRepository getAbstractTaskRepository() {
        return abstractTaskRepository;
    }

    public String getPropertyValue(String name) {
        Validate.notEmpty(name, "Property name must not be null or empty.");
        return environment.getProperty(name);
    }

    /**
     * It's here only because of some IDEs - it's not properly filtering resources during maven build.
     * "describe" variable is not replaced.
     *
     * @return "unknown" instead of "git describe" for current build.
     */
    @Deprecated
    public String getDescribe() {
        return getString("GizmoApplication.projectVersionUnknown");
    }
}
