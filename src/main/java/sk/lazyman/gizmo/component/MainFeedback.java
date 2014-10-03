package sk.lazyman.gizmo.component;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;

/**
 * @author lazyman
 */
public class MainFeedback extends org.apache.wicket.markup.html.panel.FeedbackPanel {

    public MainFeedback(String id) {
        super(id);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        switch (message.getLevel()) {
            case FeedbackMessage.ERROR:
            case FeedbackMessage.FATAL:
                return "alert-danger";
            case FeedbackMessage.INFO:
                return "alert-info";
            case FeedbackMessage.SUCCESS:
                return "alert-success";
            case FeedbackMessage.WARNING:
            case FeedbackMessage.DEBUG:
            case FeedbackMessage.UNDEFINED:
            default:
                return "alert-warning";
        }
    }

    @Override
    protected FeedbackMessagesModel newFeedbackMessagesModel() {
        return super.newFeedbackMessagesModel();
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        return super.newMessageDisplayComponent(id, message);
    }
}
