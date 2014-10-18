package sk.lazyman.gizmo.component.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class FormGroup<F extends FormInput, T extends Serializable> extends SimplePanel<T> {

    private static final String ID_LABEL = "label";
    private static final String ID_INPUT_WRAPPER = "inputWrapper";
    private static final String ID_FEEDBACK = "feedback";


    public FormGroup(String id, IModel<T> value, IModel<String> label, boolean required) {
        super(id, value);

        add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                String hasError = "";
                if (getFormComponent().hasErrorMessage()) {
                    hasError = " has-error";
                }

                return getFormGroupClass() + hasError;
            }
        }));

        initLayout(label, required);
    }

    protected String getFormGroupClass() {
        return "form-group";
    }

    private void initLayout(IModel<String> labelModel, boolean required) {
        Component label = createLabel(ID_LABEL, labelModel);
        add(label);

        FormInput inputWrapper = createInput(ID_INPUT_WRAPPER, getModel(), labelModel);

        FormComponent input = inputWrapper.getFormComponent();
        input.setRequired(required);
        input.setLabel(labelModel);
        add(inputWrapper);

        FormGroupFeedback feedback = new FormGroupFeedback(ID_FEEDBACK, new ComponentFeedbackMessageFilter(input));
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    protected Component createLabel(String labelId, IModel<String> labelModel) {
        return new Label(labelId, labelModel);
    }

    protected FormInput createInput(String componentId, IModel<T> model, IModel<String> placeholder) {
        TextInput textInput = new TextInput(componentId, model);
        FormComponent input = textInput.getFormComponent();
        input.add(AttributeAppender.replace("placeholder", placeholder));

        return textInput;
    }

    public F getFormInput() {
        return (F) get(ID_INPUT_WRAPPER);
    }

    public FormComponent getFormComponent() {
        F f = getFormInput();
        return f.getFormComponent();
    }
}
