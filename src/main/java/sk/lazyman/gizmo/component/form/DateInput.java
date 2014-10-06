package sk.lazyman.gizmo.component.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class DateInput extends FormInput {

    private static final String ID_INPUT = "input";

    public DateInput(String id, IModel model) {
        super(id, model);

        DateTextField input = new DateTextField(ID_INPUT, model);
        add(input);
    }

    @Override
    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }
}
