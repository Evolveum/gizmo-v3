package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.ReportSearchSummary;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.dto.EmailDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;

/**
 * @author lazyman
 */
@MountPath("/app/email")
public class PageEmail extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_TO = "to";
    private static final String ID_CC = "cc";
    private static final String ID_BCC = "bcc";
    private static final String ID_BODY = "body";
    private static final String ID_SEND = "send";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SUMMARY = "summary";
    private static final String ID_TABLE = "table";

    private IModel<EmailDto> model = new Model<>(new EmailDto());
    private IModel<WorkFilterDto> filter;

    public PageEmail() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        FormGroup to = new FormGroup(ID_TO, new PropertyModel<String>(model, EmailDto.F_TO),
                createStringResource("PageEmail.to"), true);
        form.add(to);

        FormGroup cc = new FormGroup(ID_CC, new PropertyModel<String>(model, EmailDto.F_CC),
                createStringResource("PageEmail.cc"), false);
        form.add(cc);

        FormGroup bcc = new FormGroup(ID_BCC, new PropertyModel<String>(model, EmailDto.F_BCC),
                createStringResource("PageEmail.bcc"), false);
        form.add(bcc);

        AreaFormGroup body = new AreaFormGroup(ID_BODY, new PropertyModel<String>(model, EmailDto.F_BODY),
                createStringResource("PageEmail.body"), false);
        form.add(body);

//        ReportSearchSummary summary = new ReportSearchSummary(ID_SUMMARY);
//        add(summary);

        WebMarkupContainer summary = new WebMarkupContainer(ID_SUMMARY);
        add(summary);

        WebMarkupContainer table = new WebMarkupContainer(ID_TABLE);
        add(table);

        initButtons(form);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton send = new AjaxSubmitButton(ID_SEND, createStringResource("GizmoApplication.button.send")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };
        form.add(send);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void sendPerformed(AjaxRequestTarget target) {

    }

    private void cancelPerformed(AjaxRequestTarget target) {

    }
}
