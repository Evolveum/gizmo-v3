package sk.lazyman.gizmo.web;

import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.MainFeedback;
import sk.lazyman.gizmo.component.form.FormUtils;
import sk.lazyman.gizmo.security.GizmoAuthWebSession;
import sk.lazyman.gizmo.web.app.PageDashboard;

/**
 * @author lazyman
 */
@MountPath("/login")
public class PageLogin extends PageTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_USERNAME = "username";
    private static final String ID_USERNAME_GROUP = "usernameGroup";
    private static final String ID_PASSWORD = "password";
    private static final String ID_PASSWORD_GROUP = "passwordGroup";
    private static final String ID_BTN_SIGNIN = "signin";
    private static final String ID_FEEDBACK = "feedback";

    private IModel<LoginDto> model = new Model(new LoginDto());

    public PageLogin() {
        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new LessResourceReference(PageLogin.class, "PageLogin.less")));
    }

    private void initLayout() {
        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form form = new Form(ID_FORM);
        add(form);

        RequiredTextField username = new RequiredTextField(ID_USERNAME, new PropertyModel(model, LoginDto.F_USERNAME));
        FormUtils.addPlaceholderAndLabel(username, createStringResource("PageLogin.username"));
        form.add(FormUtils.createFormGroup(ID_USERNAME_GROUP, username));

        PasswordTextField password = new PasswordTextField(ID_PASSWORD, new PropertyModel(model, LoginDto.F_PASSWORD));
        FormUtils.addPlaceholderAndLabel(password, createStringResource("PageLogin.password"));
        form.add(FormUtils.createFormGroup(ID_PASSWORD_GROUP, password));

        AjaxSubmitLink signin = new AjaxSubmitLink(ID_BTN_SIGNIN) {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form, PageLogin.this.get(ID_FEEDBACK));
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                loginPerformed(target, form);
            }
        };
        form.add(signin);
    }

    private void loginPerformed(AjaxRequestTarget target, Form<?> form) {
        target.add(form, PageLogin.this.get(ID_FEEDBACK));

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();

        LoginDto dto = model.getObject();
        if (session.authenticate(dto.getUsername(), dto.getPassword())) {
            setResponsePage(PageDashboard.class);
        }
    }
}
