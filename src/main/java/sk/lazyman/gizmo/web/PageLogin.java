package sk.lazyman.gizmo.web;

import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/login")
public class PageLogin extends PageTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_USERNAME = "username";
    private static final String ID_PASSWORD = "password";
    private static final String ID_BTN_SIGNIN = "signin";

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
        Form form = new Form(ID_FORM);
        add(form);

        TextField username = new TextField(ID_USERNAME, new PropertyModel(model, LoginDto.F_USERNAME));
        form.add(username);

        PasswordTextField password = new PasswordTextField(ID_PASSWORD, new PropertyModel(model, LoginDto.F_PASSWORD));
        form.add(password);

        AjaxSubmitLink signin = new AjaxSubmitLink(ID_BTN_SIGNIN) {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

            }
        };
        form.add(signin);
    }
}
