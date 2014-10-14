package sk.lazyman.gizmo.component.form;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class HFormGroup<F extends FormInput, T extends Serializable> extends SimplePanel<T> {

    private static final String ID_LABEL = "label";
    private static final String ID_INPUT_WRAPPER = "inputWrapper";
    private static final String ID_FEEDBACK = "feedback";
    private static final String ID_FEEDBACK_WRAPPER = "feedbackWrapper";


    public HFormGroup(String id, IModel<T> value, IModel<String> label, String labelSize, String inputSize,
                      String feedbackSize, boolean required) {
        super(id, value);

        add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                String hasError = "";
                if (getFormComponent().hasErrorMessage()) {
                    hasError = " has-error";
                }

                return "form-group" + hasError;
            }
        }));

        initLayout(label, labelSize, inputSize, feedbackSize, required);
    }

    private void initLayout(IModel<String> labelModel, String labelSize, String textSize, String feedbackSize,
                            boolean required) {

        Label label = new Label(ID_LABEL, labelModel);
        if (StringUtils.isNotEmpty(labelSize)) {
            label.add(AttributeAppender.prepend("class", labelSize));
        }
        add(label);

        FormInput inputWrapper = createInput(ID_INPUT_WRAPPER, getModel(), labelModel);
        if (StringUtils.isNotEmpty(textSize)) {
            inputWrapper.add(AttributeAppender.prepend("class", textSize));
        }
        FormComponent input = inputWrapper.getFormComponent();
        input.setRequired(required);
        input.setLabel(labelModel);
        add(inputWrapper);

        WebMarkupContainer feedbackWrapper = new WebMarkupContainer(ID_FEEDBACK_WRAPPER);
        if (StringUtils.isNotEmpty(textSize)) {
            inputWrapper.add(AttributeAppender.prepend("class", feedbackSize));
        }
        add(feedbackWrapper);

        FormGroupFeedback feedback = new FormGroupFeedback(ID_FEEDBACK, new ComponentFeedbackMessageFilter(input));
        feedback.setOutputMarkupId(true);
        feedbackWrapper.add(feedback);
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
