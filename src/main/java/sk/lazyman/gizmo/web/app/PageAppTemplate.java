package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarExternalLink;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import sk.lazyman.gizmo.component.MainFeedback;
import sk.lazyman.gizmo.component.TopMenuItem;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.web.PageTemplate;

/**
 * @author lazyman
 */
public class PageAppTemplate extends PageTemplate {

    private static final String ID_NAVBAR = "navbar";
    private static final String ID_TITLE = "titleHeader";
    private static final String ID_FEEDBACK = "feedback";

    public PageAppTemplate() {
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

    protected MainFeedback getFeedbackPanel() {
        return (MainFeedback) get(ID_FEEDBACK);
    }

    private PageParameters createUserPageParams() {
        PageParameters params = new PageParameters();

        GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
        params.set(PageUser.USER_ID, principal.getUserId());

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
}
