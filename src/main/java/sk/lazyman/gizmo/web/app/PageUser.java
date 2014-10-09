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
import sk.lazyman.gizmo.component.form.FormGroup;
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
    private static final String ID_SAVE = "save";
    private static final String ID_CANCEL = "cancel";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-5 col-md-4";
    private static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

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

        FormGroup username = new FormGroup(ID_NAME, new PropertyModel<String>(model, User.F_NAME),
                createStringResource("User.name"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(username);

        FormGroup firstName = new FormGroup(ID_GIVEN_NAME, new PropertyModel<String>(model, User.F_GIVEN_NAME),
                createStringResource("User.givenName"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(firstName);

        FormGroup lastName = new FormGroup(ID_FAMILY_NAME, new PropertyModel<String>(model, User.F_FAMILY_NAME),
                createStringResource("User.familyName"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(lastName);

        FormGroup email = new FormGroup(ID_LDAP_DN, new PropertyModel<String>(model, User.F_LDAP_DN),
                createStringResource("User.ldapDn"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
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
            //todo show error message
            target.add(getFeedbackPanel());
        }
    }
}
