package sk.lazyman.gizmo.component.form;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

/**
 * @author lazyman
 */
public class AreaFormGroup extends SimplePanel<String> {

    private static final String ID_TEXT = "text";
    private static final String ID_TEXT_WRAPPER = "textWrapper";
    private static final String ID_LABEL = "label";
    private static final String ID_FEEDBACK_WRAPPER = "feedbackWrapper";
    private static final String ID_FEEDBACK = "feedback";

    private int rows = 2;

    public AreaFormGroup(String id, IModel<String> value, IModel<String> label, String labelSize, String textSize,
                         String feedbackSize, boolean required) {
        super(id, value);

        add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                String hasError = "";
                if (getField().hasErrorMessage()) {
                    hasError = " has-error";
                }

                return "form-group" + hasError;
            }
        }));

        initLayout(label, labelSize, textSize, feedbackSize, required);
    }

    private void initLayout(IModel<String> label, String labelSize, String textSize, String feedbackSize,
                            boolean required) {

        Label l = new Label(ID_LABEL, label);
        if (StringUtils.isNotEmpty(labelSize)) {
            l.add(AttributeAppender.prepend("class", labelSize));
        }
        add(l);

        WebMarkupContainer textWrapper = new WebMarkupContainer(ID_TEXT_WRAPPER);
        if (StringUtils.isNotEmpty(textSize)) {
            textWrapper.add(AttributeAppender.prepend("class", textSize));
        }
        add(textWrapper);

        TextArea text = createText(getModel(), label, required);
        text.setLabel(label);
        textWrapper.add(text);

        WebMarkupContainer feedbackWrapper = new WebMarkupContainer(ID_FEEDBACK_WRAPPER);
        if (StringUtils.isNotEmpty(textSize)) {
            textWrapper.add(AttributeAppender.prepend("class", feedbackSize));
        }
        add(feedbackWrapper);

        FormGroupFeedback feedback = new FormGroupFeedback(ID_FEEDBACK, new ComponentFeedbackMessageFilter(text));
        feedback.setOutputMarkupId(true);
        feedbackWrapper.add(feedback);
    }

    protected TextArea createText(IModel<String> model, IModel<String> label, boolean required) {
        TextArea text = new TextArea(ID_TEXT, model);
        text.setRequired(required);
        text.add(AttributeAppender.replace("placeholder", label));
        text.add(AttributeAppender.replace("rows", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(rows);
            }
        }));

        return text;
    }

    public TextArea getField(){
        return (TextArea) get(ID_TEXT_WRAPPER + ":" + ID_TEXT);
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
