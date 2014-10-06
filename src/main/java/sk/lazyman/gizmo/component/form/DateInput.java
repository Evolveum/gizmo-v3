package sk.lazyman.gizmo.component.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class DateInput extends FormInput {

    public DateInput(String id, IModel model) {
        super(id, model);

        DateTextField input = new DateTextField(ID_INPUT, model);
        add(input);
    }
}
