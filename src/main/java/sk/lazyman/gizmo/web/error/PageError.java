package sk.lazyman.gizmo.web.error;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.WebResponse;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.MainFeedback;
import sk.lazyman.gizmo.component.VisibleEnableBehaviour;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.app.PageDashboard;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base class for error web pages.
 *
 * @author lazyman
 */
@MountPath("/error")
public class PageError extends PageTemplate {

    private static final String ID_MESSAGE = "message";
    private static final String ID_BACK = "back";
    private static final String ID_TITLE = "titleHeader";
    private static final String ID_FEEDBACK = "feedback";

    private Integer code;
    private String exClass;
    private String exMessage;

    public PageError() {
        this(500);
    }

    public PageError(Integer code) {
        this(code, null);
    }

    public PageError(Exception ex) {
        this(500, ex);
    }

    public PageError(Integer code, Exception ex) {
        this.code = code;

        if (ex != null) {
            exClass = ex.getClass().getName();
            exMessage = ex.getMessage();
        }

        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        final IModel<String> message = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (exClass == null) {
                    return null;
                }

                SimpleDateFormat df = new SimpleDateFormat();
                return df.format(new Date()) + "\t" + exClass + ": " + exMessage;
            }
        };

        Label label = new Label(ID_MESSAGE, message);
        label.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return StringUtils.isNotEmpty(message.getObject());
            }
        });
        add(label);

        AjaxButton back = new AjaxButton(ID_BACK, createStringResource("PageError.button.back")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(PageDashboard.class);
            }
        };
        add(back);

        MainFeedback feedback = new MainFeedback(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    private int getCode() {
        return code != null ? code : 500;
    }

    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);

        response.setStatus(getCode());
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    protected MainFeedback getFeedbackPanel() {
        return (MainFeedback) get(ID_FEEDBACK);
    }
}
