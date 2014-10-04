package sk.lazyman.gizmo.component.form;

import org.apache.wicket.feedback.IFeedbackMessageFilter;

/**
 * @author lazyman
 */
public class FormGroupFeedback extends org.apache.wicket.markup.html.panel.FeedbackPanel {

    public FormGroupFeedback(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }
}
