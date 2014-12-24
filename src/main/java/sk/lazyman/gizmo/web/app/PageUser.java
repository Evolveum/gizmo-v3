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
import sk.lazyman.gizmo.component.form.HCheckFormGroup;
import sk.lazyman.gizmo.component.form.HFormGroup;
import sk.lazyman.gizmo.component.form.HPasswordGroup;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.repository.UserRepository;
import sk.lazyman.gizmo.util.LoadableModel;

/**
 * @author lazyman
 */
@MountPath("/app/user")
public class PageUser extends PageAppUsers {

    public static final String USER_ID = "userId";

    private static final String ID_FORM = "form";
    private static final String ID_NAME = "name";
    private static final String ID_GIVEN_NAME = "givenName";
    private static final String ID_FAMILY_NAME = "familyName";
    private static final String ID_LDAP_DN = "ldapDn";
    private static final String ID_ENABLED = "enabled";
    private static final String ID_PASSWORD = "password";
    private static final String ID_SAVE = "save";
    private static final String ID_CANCEL = "cancel";

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
        Integer userId = getIntegerParam(USER_ID);

        if (userId == null) {
            return new User();
        }

        UserRepository repo = getUserRepository();
        return repo.findOne(userId);
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HFormGroup username = new HFormGroup(ID_NAME, new PropertyModel<String>(model, User.F_NAME),
                createStringResource("User.name"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(username);

        HFormGroup firstName = new HFormGroup(ID_GIVEN_NAME, new PropertyModel<String>(model, User.F_GIVEN_NAME),
                createStringResource("User.givenName"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(firstName);

        HFormGroup lastName = new HFormGroup(ID_FAMILY_NAME, new PropertyModel<String>(model, User.F_FAMILY_NAME),
                createStringResource("User.familyName"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(lastName);

        HFormGroup email = new HFormGroup(ID_LDAP_DN, new PropertyModel<String>(model, User.F_LDAP_DN),
                createStringResource("User.ldapDn"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, false);
        form.add(email);

        HFormGroup enabled = new HCheckFormGroup(ID_ENABLED, new PropertyModel<Boolean>(model, User.F_ENABLED),
                createStringResource("User.enabled"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(enabled);

        HFormGroup password = new HPasswordGroup(ID_PASSWORD, new PropertyModel<String>(model, User.F_PASSWORD),
                createStringResource("User.password"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, false);
        form.add(password);

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

            PageUsers next = new PageUsers();
            next.success(getString("Message.userSavedSuccessfully"));
            setResponsePage(next);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveUser", ex, target);
        }
    }
}
