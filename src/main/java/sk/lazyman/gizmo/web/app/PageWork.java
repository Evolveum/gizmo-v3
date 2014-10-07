package sk.lazyman.gizmo.web.app;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.*;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.repository.UserRepository;
import sk.lazyman.gizmo.repository.WorkRepository;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/work")
public class PageWork extends PageAppTemplate {

    public static final String WORK_ID = "workId";

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TRACK_ID = "trackId";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_IS_WORK_LOG = "isWorkLog";
    private static final String ID_PART = "part";
    private static final String ID_CUSTOMER = "customer";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-5 col-md-4";
    private static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

    private IModel<List<User>> users;
    private IModel<Work> model;

    public PageWork() {
        model = new LoadableModel<Work>(false) {

            @Override
            protected Work load() {
                return loadWork();
            }
        };

        users= new LoadableModel<List<User>>(false) {

            @Override
            protected List<User> load() {
                return loadUsers();
            }
        };

        initLayout();
    }

    private List<User> loadUsers() {
        UserRepository repository = getUserRepository();
        return repository.listUsersOrderByGivenFamilyName();
    }

    private Work loadWork() {
        PageParameters params = getPageParameters();
        StringValue val = params.get(WORK_ID);
        String workId = val != null ? val.toString() : null;

        if (workId == null || !workId.matches("[0-9]+")) {
            return new Work();
        }

        WorkRepository repository = getWorkRepository();
        Work work = repository.findOne(Integer.parseInt(workId));
        if (work == null) {
            getSession().error(translateString("Message.couldntFindWork", workId));
            throw new RestartResponseException(PageWork.class);
        }

        return work;
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        DropDownFormGroup<User> realizator = new DropDownFormGroup<>(ID_REALIZATOR,
                new PropertyModel<User>(model, Work.F_REALIZATOR),
                createStringResource("Work.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        realizator.setRenderer(new IChoiceRenderer<User>() {

            @Override
            public Object getDisplayValue(User object) {
                return object != null ? object.getFullName() : null;
            }

            @Override
            public String getIdValue(User object, int index) {
                return Integer.toString(index);
            }
        });
        realizator.setChoices(users);
        form.add(realizator);

        CheckFormGroup isWorkLog = new CheckFormGroup(ID_IS_WORK_LOG, new Model<>(Boolean.TRUE),
                createStringResource("PageWork.isWorkLog"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        FormComponent check = isWorkLog.getFormComponent();
        check.add(new AjaxFormComponentUpdatingBehavior("click") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //todo not working
                System.out.println("asdf");
            }
        });
        form.add(isWorkLog);

        FormGroup customer = new FormGroup(ID_CUSTOMER, new Model(),
                createStringResource("Work.customer"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(customer);

        FormGroup part = new FormGroup(ID_PART, new Model(),
                createStringResource("Work.part"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(part);

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

        initButtons(form);
    }

    private void initButtons(Form form) {
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
