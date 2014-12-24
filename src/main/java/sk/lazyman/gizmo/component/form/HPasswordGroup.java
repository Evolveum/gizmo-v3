package sk.lazyman.gizmo.component.form;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class HPasswordGroup extends HFormGroup<PasswordInput, String> {

    public HPasswordGroup(String id, IModel<String> value, IModel<String> label,
                          String labelSize, String inputSize, String feedbackSize, boolean required) {
        super(id, value, label, labelSize, inputSize, feedbackSize, required);
    }

    @Override
    protected FormInput createInput(String componentId, IModel<String> model, IModel<String> placeholder) {
        PasswordInput passwordInput = new PasswordInput(componentId, model);
        FormComponent input = passwordInput.getFormComponent();
        input.add(AttributeAppender.replace("placeholder", placeholder));

        return passwordInput;
    }
}
