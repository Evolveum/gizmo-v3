package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import sk.lazyman.gizmo.data.TaskType;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.dto.BulkDto;
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
@MountPath("/app/to")
public class PageBulk extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_PART = "part";

    private static final int MAX_BULK_CREATE = 20;

    private IModel<List<User>> users = GizmoUtils.createUsersModel(this);
    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, true, true);

    private IModel<BulkDto> model;

    public PageBulk() {
        model = new LoadableModel<BulkDto>(false) {

            @Override
            protected BulkDto load() {
                BulkDto dto = new BulkDto();
                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                dto.setFrom(new Date());

                return dto;
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HDropDownFormGroup<User> realizator = new HDropDownFormGroup<>(ID_REALIZATOR,
                new PropertyModel<User>(model, BulkDto.F_REALIZATOR),
                createStringResource("AbstractTask.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        realizator.setRenderer(GizmoUtils.createUserChoiceRenderer());
        realizator.setChoices(users);
        form.add(realizator);

        HFormGroup part = new HFormGroup<AutoCompleteInput, CustomerProjectPartDto>(ID_PART,
                new PropertyModel<CustomerProjectPartDto>(model, BulkDto.F_PART),
                createStringResource("Work.part"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true) {

            @Override
            protected FormInput createInput(String componentId, IModel<CustomerProjectPartDto> model,
                                            IModel<String> placeholder) {
                AutoCompleteInput formInput = new AutoCompleteInput(componentId, model, projects);
                FormComponent input = formInput.getFormComponent();
                input.add(AttributeAppender.replace("placeholder", placeholder));

                return formInput;
            }
        };
        form.add(part);
        FormComponent partText = part.getFormComponent();
        partText.add(new AjaxFormComponentUpdatingBehavior("blur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });

        HFormGroup from = new HDateFormGroup(ID_FROM, new PropertyModel<Date>(model, BulkDto.F_FROM),
                createStringResource("PageBulk.from"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(from);

        HFormGroup to = new HDateFormGroup(ID_TO, new PropertyModel<Date>(model, BulkDto.F_TO),
                createStringResource("PageBulk.to"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(to);

        HAreaFormGroup description = new HAreaFormGroup(ID_DESCRIPTION, new
                PropertyModel<String>(model, BulkDto.F_DESCRIPTION),
                createStringResource("AbstractTask.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        description.setRows(5);
        form.add(description);

        initButtons(form);
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
            BulkDto bulk = model.getObject();

            PartRepository parts = getProjectPartRepository();
            CustomerProjectPartDto partDto = bulk.getPart();
            Part part = parts.getOne(partDto.getPartId());

            Date date = GizmoUtils.clearTime(bulk.getFrom());
            Date to = GizmoUtils.clearTime(bulk.getTo());
            to = GizmoUtils.removeOneMilis(GizmoUtils.addOneDay(to));

            int count = 0;
            while (date.before(to)) {
                System.out.println(date + "\t->\t" + to);
                Work work = createWork(bulk, part, date);
                repository.save(work);

                count++;
                date = GizmoUtils.addOneDay(date);

                if (count > MAX_BULK_CREATE) {
                    break;
                }
            }
            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.workSavedSuccessfully").getString());
            if (count > MAX_BULK_CREATE) {
                response.warn(createStringResource("Message.bulkStopped", MAX_BULK_CREATE).getString());
            }
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }

    private Work createWork(BulkDto bulk, Part part, Date date) {
        Work work = new Work();
        work.setRealizator(bulk.getRealizator());
        work.setPart(part);
        work.setDate(date);
        work.setDescription(bulk.getDescription());
        work.setInvoiceLength(0);
        work.setWorkLength(8);
        work.setType(TaskType.WORK);

        return work;
    }
}
