package sk.lazyman.gizmo.component.form;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author lazyman
 */
public class PasswordInput extends FormInput<String> {

    private static final String ID_INPUT_2 = "input2";

    public PasswordInput(String id, IModel<String> model) {
        super(id, model);

        PasswordTextField text = new PasswordTextField(ID_INPUT, createCustomInputModel(model));
        text.setRequired(false);
        add(text);

        PasswordTextField text2 = new PasswordTextField(ID_INPUT_2, new Model<String>());
        text2.setRequired(false);
        text2.setLabel(createStringResource("PasswordInput.confirmPassword"));
        add(text2);
    }

    private IModel<String> createCustomInputModel(final IModel<String> model) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                return model.getObject();
            }

            @Override
            public void setObject(String object) {
                if (StringUtils.isEmpty(object)) {
                    return;
                }
                model.setObject(object);
            }

            @Override
            public void detach() {
            }
        };
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        FormComponent fc1 = (FormComponent) get(ID_INPUT);
        FormComponent fc2 = (FormComponent) get(ID_INPUT_2);

        Form form = findParent(Form.class);
        form.add(new EqualPasswordInputValidator(fc2, fc1));
    }
}
