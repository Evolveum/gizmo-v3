package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.DateFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.Date;

/**
 * @author lazyman
 */
@MountPath("/app/work")
public class PageWork extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TRACK_ID = "trackId";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_ = "";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-5 col-md-4";
    private static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

    private IModel<Work> model;

    public PageWork() {
        model = new LoadableModel<Work>(false) {

            @Override
            protected Work load() {
                return new Work();
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        FormGroup username = new FormGroup(ID_REALIZATOR, new PropertyModel<String>(model, Work.F_REALIZATOR),
                createStringResource("Work.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(username);

        FormGroup date = new DateFormGroup(ID_DATE, new PropertyModel<Date>(model, Work.F_DATE),
                createStringResource("Work.date"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(date);

        FormGroup invoice = new FormGroup(ID_INVOICE, new PropertyModel<String>(model, Work.F_INVOICE_LENGTH),
                createStringResource("Work.invoiceLength"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(invoice);

        FormGroup length = new FormGroup(ID_LENGTH, new PropertyModel<String>(model, Work.F_WORK_LENGTH),
                createStringResource("Work.workLength"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(length);

        AreaFormGroup description = new AreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(model, Work.F_DESCRIPTION),
                createStringResource("Work.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        description.setRows(5);
        form.add(description);

        FormGroup trackId = new FormGroup(ID_TRACK_ID, new PropertyModel<String>(model, Work.F_TRACK_ID),
                createStringResource("Work.trackId"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(trackId);

        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                userSavePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };
        form.add(save);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageDashboard.class);
    }

    private void userSavePerformed(AjaxRequestTarget target) {
        //todo implement
    }
}
