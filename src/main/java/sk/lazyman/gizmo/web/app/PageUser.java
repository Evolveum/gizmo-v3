package sk.lazyman.gizmo.web.app;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.TextFormGroup;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;
import sk.lazyman.gizmo.util.LoadableModel;

/**
 * @author lazyman
 */
@MountPath("/app/user")
public class PageUser extends PageAppTemplate {

    public static final String USER_ID = "userId";

    private static final String ID_FORM = "form";
    private static final String ID_USERNAME = "username";
    private static final String ID_FIRST_NAME = "firstName";
    private static final String ID_LAST_NAME = "lastName";
    private static final String ID_EMAIL = "email";
    private static final String ID_ROLE = "role";
    private static final String ID_SAVE = "save";
    private static final String ID_CANCEL = "cancel";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-7 col-md-4";

    private IModel<User> model;

    public PageUser() {
        model = new LoadableModel<User>(false) {

            @Override
            protected User load() {
                return loadUser();
            }
        };

        initLayout();
    }

    private User loadUser() {
        StringValue id = getPageParameters().get(USER_ID);
        String userId = id != null ? id.toString() : null;

        if (StringUtils.isEmpty(userId)) {
            return new User();
        }

        UserRepository repo = getUserRepository();
        return repo.findOne(new Integer(userId));
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextFormGroup username = new TextFormGroup(ID_USERNAME, new PropertyModel<String>(model, User.F_USER_NAME),
                createStringResource("User.userName"), LABEL_SIZE, TEXT_SIZE, true);
        form.add(username);

        TextFormGroup firstName = new TextFormGroup(ID_FIRST_NAME, new PropertyModel<String>(model, User.F_FIRST_NAME),
                createStringResource("User.firstName"), LABEL_SIZE, TEXT_SIZE, true);
        form.add(firstName);

        TextFormGroup lastName = new TextFormGroup(ID_LAST_NAME, new PropertyModel<String>(model, User.F_LAST_NAME),
                createStringResource("User.lastName"), LABEL_SIZE, TEXT_SIZE, true);
        form.add(lastName);

        TextFormGroup email = new TextFormGroup(ID_EMAIL, new PropertyModel<String>(model, User.F_EMAIL),
                createStringResource("User.email"), LABEL_SIZE, TEXT_SIZE, true);
        form.add(email);

        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                userSavePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };
        form.add(save);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageUsers.class);
    }

    private void userSavePerformed(AjaxRequestTarget target) {
        try {
            UserRepository repo = getUserRepository();
            repo.saveAndFlush(model.getObject());

            setResponsePage(PageUsers.class);
        } catch (Exception ex) {
            target.add(getFeedbackPanel());
        }
    }
}
