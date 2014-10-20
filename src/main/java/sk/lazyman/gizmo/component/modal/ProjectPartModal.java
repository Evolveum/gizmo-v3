package sk.lazyman.gizmo.component.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.*;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.data.Part;

/**
 * @author lazyman
 */
public class ProjectPartModal extends Modal<Part> {

    private static final String ID_FORM = "form";
    private static final String ID_LABEL = "label";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_PROJECT = "project";
    private static final String ID_SAVE = "save";
    private static final String ID_CANCEL = "cancel";

    public ProjectPartModal(String id) {
        super(id, new Model<>(new Part()));

        header(createTitle());
        initLayout();
    }

    private IModel<String> createTitle() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Part part = getModelObject();

                String key = part.getId() != null ? "ProjectPartModal.edit" : "ProjectPartModal.new";
                return createStringResource(key).getObject();
            }
        };
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        FormGroup name = new FormGroup(ID_NAME,
                new PropertyModel<String>(getModel(), Part.F_NAME),
                createStringResource("Part.name"), true);
        form.add(name);

        FormGroup description = new AreaFormGroup(ID_DESCRIPTION,
                new PropertyModel<String>(getModel(), Part.F_DESCRIPTION),
                createStringResource("Part.description"), true);
        form.add(description);

        initButtons(form);
    }

    private IModel<String> createStringResource(String key) {
        return new StringResourceModel(key, this, null);
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

    protected void cancelPerformed(AjaxRequestTarget target) {
        close(target);
    }

    protected void savePerformed(AjaxRequestTarget target) {
        //todo implement
    }
}
