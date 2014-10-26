package sk.lazyman.gizmo.web.app;

import org.apache.commons.lang.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.*;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.repository.PartRepository;
import sk.lazyman.gizmo.repository.WorkRepository;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
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
    private static final String ID_PART = "part";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-5 col-md-4";
    private static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

    private IModel<List<User>> users = GizmoUtils.createUsersModel(this);
    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, true, true);

    private IModel<Work> model;

    public PageWork() {
        model = new LoadableModel<Work>(false) {

            @Override
            protected Work load() {
                return loadWork();
            }
        };

        initLayout();
    }

    public PageWork(IModel<Work> model) {
        Validate.notNull(model, "Model must not be null.");
        this.model = model;

        initLayout();
    }

    private Work loadWork() {
        Integer workId = getIntegerParam(WORK_ID);
        if (workId == null) {
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            User user = principal.getUser();

            Work work = new Work();
            work.setRealizator(user);
            work.setDate(new Date());

            return work;
        }

        WorkRepository repository = getWorkRepository();
        Work work = repository.findOne(workId);
        if (work == null) {
            getSession().error(translateString("Message.couldntFindWork", workId));
            throw new RestartResponseException(PageWork.class);
        }

        return work;
    }

    private <T extends FormInput> void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HDropDownFormGroup<User> realizator = new HDropDownFormGroup<>(ID_REALIZATOR,
                new PropertyModel<User>(model, Work.F_REALIZATOR),
                createStringResource("AbstractTask.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        realizator.setRenderer(GizmoUtils.createUserChoiceRenderer());
        realizator.setChoices(users);
        form.add(realizator);

        HFormGroup part = new HFormGroup<T, Part>(ID_PART, new PropertyModel<Part>(model, Work.F_PART),
                createStringResource("Work.part"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true) {

            @Override
            protected FormInput createInput(String componentId, IModel<Part> model, IModel<String> placeholder) {
                AutoCompleteInput formInput = new AutoCompleteInput(componentId, createPartModel(model), projects);
                FormComponent input = formInput.getFormComponent();
                input.add(AttributeAppender.replace("placeholder", placeholder));

                return formInput;
            }
        };
        form.add(part);

        HFormGroup date = new HDateFormGroup(ID_DATE, new PropertyModel<Date>(model, Work.F_DATE),
                createStringResource("AbstractTask.date"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(date);

        HFormGroup invoice = new HFormGroup(ID_INVOICE, new PropertyModel<String>(model, Work.F_INVOICE_LENGTH),
                createStringResource("Work.invoiceLength"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(invoice);

        HFormGroup length = new HFormGroup(ID_LENGTH, new PropertyModel<String>(model, Work.F_WORK_LENGTH),
                createStringResource("AbstractTask.workLength"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(length);

        HAreaFormGroup description = new HAreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(model, Work.F_DESCRIPTION),
                createStringResource("AbstractTask.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        description.setRows(5);
        form.add(description);

        HFormGroup trackId = new HFormGroup(ID_TRACK_ID, new PropertyModel<String>(model, Work.F_TRACK_ID),
                createStringResource("AbstractTask.trackId"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, false);
        form.add(trackId);

        initButtons(form);
    }

    private IModel<CustomerProjectPartDto> createPartModel(final IModel<Part> model) {
        return new IModel<CustomerProjectPartDto>() {

            private Part part;

            @Override
            public CustomerProjectPartDto getObject() {
                part = model.getObject();

                if (part != null) {
                    for (CustomerProjectPartDto dto : projects.getObject()) {
                        if (part.getId().equals(dto.getPartId())) {
                            return dto;
                        }
                    }
                }

                return null;
            }

            @Override
            public void setObject(CustomerProjectPartDto object) {
                if (object == null) {
                    model.setObject(null);
                }

                Integer id = object.getPartId();
                if (part != null && id.equals(part.getId())) {
                    model.setObject(part);
                }

                PartRepository repository = getProjectPartRepository();
                part = repository.findOne(id);
                model.setObject(part);
            }

            @Override
            public void detach() {
            }
        };
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saveWorkPerformed(target);
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

    private void saveWorkPerformed(AjaxRequestTarget target) {
        WorkRepository repository = getWorkRepository();
        try {
            Work work = model.getObject();
            work = repository.save(work);

            model.setObject(work);

            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.workSavedSuccessfully").getString());
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
