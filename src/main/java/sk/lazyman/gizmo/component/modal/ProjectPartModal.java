package sk.lazyman.gizmo.component.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.*;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.data.Part;

/**
 * @author lazyman
 */
public class ProjectPartModal extends Modal<Part> {

    private static final String ID_LABEL = "label";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_PROJECT = "project";

    private IModel<Part> partModel;

    public ProjectPartModal(String id) {
        this(id, new Model<>(new Part()));
    }

    public ProjectPartModal(String id, IModel<Part> partModel) {
        super(id);

        this.partModel = partModel;

        header(createTitle());
        initLayout();
    }

    private IModel<String> createTitle() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Part part = partModel.getObject();

                String key = part.getId() != null ? "ProjectPartModal.edit" : "ProjectPartModal.new";
                return createStringResource(key).getObject();
            }
        };
    }

    private void initLayout() {
        final FormGroup name = new FormGroup(ID_NAME,
                new PropertyModel<String>(partModel, Part.F_NAME),
                createStringResource("Part.name"), true);
        name.setOutputMarkupId(true);
        add(name);

        final FormGroup description = new AreaFormGroup(ID_DESCRIPTION,
                new PropertyModel<String>(partModel, Part.F_DESCRIPTION),
                createStringResource("Part.description"), false);
        description.setOutputMarkupId(true);
        add(description);

        BootstrapAjaxButton cancel = new BootstrapAjaxButton(BUTTON_MARKUP_ID,
                createStringResource("GizmoApplication.button.cancel"), Buttons.Type.Default) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed(target);
            }
        };
        addButton(cancel);

        BootstrapAjaxButton save = new BootstrapAjaxButton(BUTTON_MARKUP_ID,
                createStringResource("GizmoApplication.button.save"), Buttons.Type.Primary) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target, partModel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(name, description);
            }
        };
        addButton(save);
    }

    public Part getPart() {
        return partModel.getObject();
    }

    public IModel<Part> getPartModel() {
        return partModel;
    }

    public void setPart(Part part) {
        this.partModel.setObject(part);
    }

    private IModel<String> createStringResource(String key) {
        return new StringResourceModel(key, this, null);
    }

    protected void cancelPerformed(AjaxRequestTarget target) {
        close(target);
    }

    protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
        close(target);
    }
}
