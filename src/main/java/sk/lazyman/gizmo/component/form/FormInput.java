package sk.lazyman.gizmo.component.form;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

/**
 * @author lazyman
 */
public abstract class FormInput<T> extends SimplePanel<T> {

    protected static final String ID_INPUT = "input";

    public FormInput(String id, IModel<T> model) {
        super(id, model);
    }

    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }
}
