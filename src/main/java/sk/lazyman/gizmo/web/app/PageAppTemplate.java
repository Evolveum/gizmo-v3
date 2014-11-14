package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarExternalLink;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.lazyman.gizmo.component.MainFeedback;
import sk.lazyman.gizmo.component.TopMenuItem;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.error.PageError;

/**
 * @author lazyman
 */
public class PageAppTemplate extends PageTemplate {

    private static final String ID_NAVBAR = "navbar";
    private static final String ID_TITLE = "titleHeader";
    private static final String ID_FEEDBACK = "feedback";

    public PageAppTemplate() {
        this(null);
    }

    public PageAppTemplate(PageParameters parameters) {
        super(parameters);

        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new LessResourceReference(PageAppTemplate.class, "PageAppTemplate.less")));
    }

    private void initLayout() {
        Navbar navbar = new Navbar(ID_NAVBAR);
        navbar.setBrandName(createStringResource("GizmoApplication.projectName"));
        navbar.setPosition(Navbar.Position.STATIC_TOP);
        add(navbar);

        TopMenuItem item = new TopMenuItem(createStringResource("PageAppTemplate.menu.dashboard"), PageDashboard.class);
        navbar.addComponents(new ImmutableNavbarComponent(item));

        item = new TopMenuItem(createStringResource("PageAppTemplate.menu.customers"), PageCustomers.class);
        navbar.addComponents(new ImmutableNavbarComponent(item));

        item = new TopMenuItem(createStringResource("PageAppTemplate.menu.projects"), PageProjects.class);
        navbar.addComponents(new ImmutableNavbarComponent(item));

        item = new TopMenuItem(createStringResource("PageAppTemplate.menu.users"), PageUsers.class);
        navbar.addComponents(new ImmutableNavbarComponent(item));

        item = new TopMenuItem(createStringResource("PageAppTemplate.menu.emails"), PageEmails.class);
        navbar.addComponents(new ImmutableNavbarComponent(item));

        item = new TopMenuItem(createUsernameModel(), PageUser.class, createUserPageParams());
        item.setActive(true);
        navbar.addComponents(new ImmutableNavbarComponent(item, Navbar.ComponentPosition.RIGHT));

        NavbarExternalLink logoutLink = new NavbarExternalLink(
                new Model<>(RequestCycle.get().getRequest().getContextPath() + "/j_spring_security_logout"));
        logoutLink.setLabel(createStringResource("PageAppTemplate.menu.logout"));
        navbar.addComponents(new ImmutableNavbarComponent(logoutLink, Navbar.ComponentPosition.RIGHT));

        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    public MainFeedback getFeedbackPanel() {
        return (MainFeedback) get(ID_FEEDBACK);
    }

    private PageParameters createUserPageParams() {
        PageParameters params = new PageParameters();

        GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
        params.set(PageUser.USER_ID, principal.getUserId());

        return params;
    }

    public Integer getIntegerParam(String paramName) {
        PageParameters params = getPageParameters();
        StringValue val = params.get(paramName);
        String id = val != null ? val.toString() : null;

        if (id == null || !id.matches("[0-9]+")) {
            return null;
        }

        return Integer.parseInt(id);
    }

    public PageParameters createPageParams(String paramName, Integer value) {
        PageParameters params = new PageParameters();
        params.add(paramName, value);
        return params;
    }

    private IModel<String> createUsernameModel() {
        return new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                return principal.getFullName();
            }
        };
    }

    public void handleGuiException(PageAppTemplate page, Exception ex, AjaxRequestTarget target) {
        handleGuiException(page, getString("Message.unknownError"), ex, target);
    }

    public void handleGuiException(PageAppTemplate page, String message, Exception ex, AjaxRequestTarget target) {
        Logger LOG = LoggerFactory.getLogger(page.getClass());
        LOG.error("Exception occurred, {}, reason: {}", message, ex.getMessage());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exception occurred, {}", ex);
        }

        if (target != null) {
            page.error(createStringResource(message, ex.getMessage()).getString());

            target.add(page.getFeedbackPanel());
        } else {
            PageError errorPage = new PageError();
            errorPage.error(createStringResource(message, ex.getMessage()).getString());

            throw new RestartResponseException(errorPage);
        }
    }
}
