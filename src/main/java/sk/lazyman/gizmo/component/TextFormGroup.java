package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class TextFormGroup extends SimplePanel<String> {

    private static final String ID_TEXT = "text";
    private static final String ID_TEXT_WRAPPER = "textWrapper";
    private static final String ID_LABEL = "label";
    private static final String ID_FEEDBACK_WRAPPER = "feedbackWrapper";
    private static final String ID_FEEDBACK = "feedback";

    public TextFormGroup(String id, IModel<String> value, IModel<String> label, String labelSize, String textSize,
                         String feedbackSize, boolean required) {
        super(id, value);

        add(AttributeModifier.append("class", "form-group"));

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

        TextField text = createText(getModel(), label, required);
        text.setLabel(label);
        textWrapper.add(text);

        WebMarkupContainer feedbackWrapper = new WebMarkupContainer(ID_FEEDBACK_WRAPPER);
        if (StringUtils.isNotEmpty(textSize)) {
            textWrapper.add(AttributeAppender.prepend("class", feedbackSize));
        }
        add(feedbackWrapper);

        FeedbackPanel feedback = new FeedbackPanel(ID_FEEDBACK, new ComponentFeedbackMessageFilter(text));
        feedback.setOutputMarkupId(true);
        feedbackWrapper.add(feedback);
    }

    protected TextField createText(IModel<String> model, IModel<String> label, boolean required) {
        TextField text = new TextField(ID_TEXT, model);
        text.setRequired(required);
        text.add(AttributeAppender.replace("placeholder", label));

        return text;
    }

    public TextField getField(){
        return (TextField) get(ID_TEXT_WRAPPER + ":" + ID_TEXT);
    }
}
