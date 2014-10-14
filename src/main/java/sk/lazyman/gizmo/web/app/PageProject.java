package sk.lazyman.gizmo.web.app;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.HAreaFormGroup;
import sk.lazyman.gizmo.component.form.HCheckFormGroup;
import sk.lazyman.gizmo.component.form.HFormGroup;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.repository.ProjectRepository;
import sk.lazyman.gizmo.util.LoadableModel;

/**
 * @author lazyman
 */
@MountPath("/app/project")
public class PageProject extends PageAppProjects {

    public static final String PROJECT_ID = "projectId";

    private static final String ID_FORM = "form";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CUSTOMER = "customer";
    private static final String ID_CLOSED = "closed";
    private static final String ID_COMMERCIAL = "commercial";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";

    private static final String LABEL_SIZE = "col-sm-3 col-md-2 control-label";
    private static final String TEXT_SIZE = "col-sm-5 col-md-4";
    private static final String FEEDBACK_SIZE = "col-sm-4 col-md-4";

    private IModel<Project> model;

    public PageProject() {
        model = new LoadableModel<Project>() {

            @Override
            protected Project load() {
                return loadProject();
            }
        };

        initLayout();
    }

    private Project loadProject() {
        PageParameters params = getPageParameters();
        StringValue val = params.get(PROJECT_ID);
        String projectId = val != null ? val.toString() : null;

        if (projectId == null || !projectId.matches("[0-9]+")) {
            return new Project();
        }

        ProjectRepository repository = getProjectRepository();
        Project project = repository.findOne(Integer.parseInt(projectId));
        if (project == null) {
            getSession().error(translateString("Message.couldntFindProject", projectId));
            throw new RestartResponseException(PageProject.class);
        }

        return project;
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HFormGroup name = new HFormGroup(ID_NAME, new PropertyModel<String>(model, Project.F_NAME),
                createStringResource("Project.name"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(name);

        HFormGroup description = new HAreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(model, Project.F_DESCRIPTION),
                createStringResource("Project.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(description);

        HFormGroup customer = new HFormGroup(ID_CUSTOMER, new PropertyModel<String>(model, Project.F_CUSTOMER),
                createStringResource("Project.customer"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(customer);

        HFormGroup closed = new HCheckFormGroup(ID_CLOSED, new PropertyModel<Boolean>(model, Project.F_CLOSED),
                createStringResource("Project.closed"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(closed);

        HFormGroup commercial = new HCheckFormGroup(ID_COMMERCIAL, new PropertyModel<Boolean>(model, Project.F_COMMERCIAL),
                createStringResource("Project.commercial"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(commercial);

        initButtons(form);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target);
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
        setResponsePage(PageProjects.class);
    }

    private void savePerformed(AjaxRequestTarget target) {
        //todo implement
    }
}
