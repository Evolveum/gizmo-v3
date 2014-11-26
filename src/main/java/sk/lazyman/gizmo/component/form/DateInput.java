package sk.lazyman.gizmo.component.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.util.GizmoUtils;

/**
 * @author lazyman
 */
public class DateInput extends FormInput {

    public DateInput(String id, IModel model) {
        super(id, model);

        DateTextField input = new DateTextField(ID_INPUT, model, GizmoUtils.DATE_FIELD_FORMAT);
        add(input);
    }
}
